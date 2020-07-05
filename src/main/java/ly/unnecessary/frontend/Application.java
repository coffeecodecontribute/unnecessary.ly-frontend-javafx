package ly.unnecessary.frontend;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.protobuf.Empty;

import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ly.unnecessary.backend.api.CommunityOuterClass.Channel;
import ly.unnecessary.backend.api.CommunityOuterClass.ChannelFilter;
import ly.unnecessary.backend.api.CommunityOuterClass.Community;
import ly.unnecessary.backend.api.CommunityOuterClass.CommunityFilter;
import ly.unnecessary.backend.api.CommunityServiceGrpc;

public class Application extends javafx.application.Application {
    public static Metadata.Key<String> USER_EMAIL_KEY = Metadata.Key.of("x-uly-email", ASCII_STRING_MARSHALLER);
    public static Metadata.Key<String> USER_PASSWORD_KEY = Metadata.Key.of("x-uly-password", ASCII_STRING_MARSHALLER);

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Setup authentication
        var ch = ManagedChannelBuilder.forTarget("localhost:1999").usePlaintext().build();

        var metadata = new Metadata();
        metadata.put(USER_EMAIL_KEY, "felix@pojtinger.com");
        metadata.put(USER_PASSWORD_KEY, "pass1234");

        // Create clients
        var communityClient = MetadataUtils.attachHeaders(CommunityServiceGrpc.newBlockingStub(ch), metadata);

        // Create components
        var communityComponent = new CommunityComponent();

        // Create handlers
        Consumer<Channel> handleChannelSwitch = (newChannel) -> {
            Platform.runLater(() -> {
                communityComponent.setChannelTitle(newChannel.getDisplayName());
                communityComponent.setSelectedChannel(newChannel);
            });

            var channelFilter = ChannelFilter.newBuilder().setChannelId(newChannel.getId()).build();

            var newChats = communityClient.listChatsForChannel(channelFilter).getChatsList();

            Platform.runLater(() -> {
                communityComponent.setChats(newChats);
                communityComponent.scrollChatsToBottom();
            });

            var stream = communityClient.subscribeToChannelChats(channelFilter);

            new Thread(() -> stream.forEachRemaining(newChat -> Platform.runLater(() -> {
                communityComponent.addChat(newChat);
                communityComponent.scrollChatsToBottom();
            }))).start();
        };

        Consumer<Community> handleCommunitySwitch = (communityToFetch) -> {
            var communityFilter = CommunityFilter.newBuilder().setCommunityId(communityToFetch.getId()).build();

            var newCommunity = communityClient.getCommunity(communityFilter);

            Platform.runLater(() -> {
                communityComponent.setSelectedCommunity(newCommunity);
                communityComponent.setCommunityTitle(newCommunity.getDisplayName());
                communityComponent.setOwner(newCommunity.getOwner());
                communityComponent.setMembers(newCommunity.getMembersList());
            });

            var newChannels = communityClient.listChannelsForCommunity(communityFilter).getChannelsList();

            Platform.runLater(() -> communityComponent.setChannels(newChannels));

            handleChannelSwitch.accept(newChannels.get(0));
        };

        Consumer<List<Community>> handleInit = (newCommunities) -> {
            Platform.runLater(() -> {
                communityComponent.setCommunities(newCommunities);

                handleCommunitySwitch.accept(newCommunities.get(0));
            });
        };

        // Connect handlers
        communityComponent.setOnSwitchCommunity((newCommunity) -> handleCommunitySwitch.accept(newCommunity));

        communityComponent.setOnSwitchChannel((newChannel) -> handleChannelSwitch.accept(newChannel));

        // Set initial state
        new Thread(() -> {
            var ownedCommunities = communityClient.listCommunitiesForOwner(Empty.newBuilder().build());
            var memberCommunities = communityClient.listCommunitiesForMember(Empty.newBuilder().build());

            var allCommunities = Stream.concat(ownedCommunities.getCommunitiesList().stream(),
                    memberCommunities.getCommunitiesList().stream()).collect(Collectors.toList());

            handleInit.accept(allCommunities);
        }).start();

        // Render initial state
        var scene = new Scene((Parent) communityComponent.render(), 1080, 720);

        primaryStage.setScene(scene);
        primaryStage.setTitle("unnecessary.ly");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}