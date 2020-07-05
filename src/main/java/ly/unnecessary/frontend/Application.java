package ly.unnecessary.frontend;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

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
import ly.unnecessary.backend.api.CommunityOuterClass.ChannelFilter;
import ly.unnecessary.backend.api.CommunityOuterClass.CommunityFilter;
import ly.unnecessary.backend.api.CommunityOuterClass.NewChat;
import ly.unnecessary.backend.api.CommunityServiceGrpc;

public class Application extends javafx.application.Application {
    public static Metadata.Key<String> USER_EMAIL_KEY = Metadata.Key.of("x-uly-email", ASCII_STRING_MARSHALLER);
    public static Metadata.Key<String> USER_PASSWORD_KEY = Metadata.Key.of("x-uly-password", ASCII_STRING_MARSHALLER);

    @Override
    public void start(Stage primaryStage) throws Exception {
        var ch = ManagedChannelBuilder.forTarget("localhost:1999").usePlaintext().build();

        var metadata = new Metadata();
        metadata.put(USER_EMAIL_KEY, "felix@pojtinger.com");
        metadata.put(USER_PASSWORD_KEY, "pass1234");

        var communityClient = MetadataUtils.attachHeaders(CommunityServiceGrpc.newBlockingStub(ch), metadata);

        var communityComponent = new CommunityComponent();

        // Queries on UI
        communityComponent.setOnCreateChat(chat -> new Thread(() -> {
            if (chat.equals("")) {
                return;
            }

            Platform.runLater(() -> communityComponent.clearAndFocusNewChatFieldText());

            var newChat = NewChat.newBuilder().setChannelId(1).setMessage(chat).build();

            communityClient.createChat(newChat);
        }).start());

        communityComponent.setOnClickCommunityLink(c -> Platform.runLater(() -> {
            communityComponent.selectCommunityLink(c);
            communityComponent.setCommunityTitle(c.getDisplayName());
        }));

        communityComponent.setOnChannelClick(c -> Platform.runLater(() -> {
            communityComponent.setChannelTitle(c.getDisplayName());
        }));

        // Mutations on UI
        new Thread(() -> {
            var channelFilter = ChannelFilter.newBuilder().setChannelId(1).build();

            var stream = communityClient.subscribeToChannelChats(channelFilter);

            stream.forEachRemaining(c -> Platform.runLater(() -> {
                communityComponent.addChat(c);

                communityComponent.scrollChatsToBottom();
            }));
        }).start();

        new Thread(() -> {
            var channelFilter = ChannelFilter.newBuilder().setChannelId(1).build();

            var chats = communityClient.listChatsForChannel(channelFilter);

            Platform.runLater(() -> {
                communityComponent.replaceChats(chats.getChatsList());

                communityComponent.scrollChatsToBottom();
            });
        }).start();

        new Thread(() -> {
            var ownedCommunities = communityClient.listCommunitiesForOwner(Empty.newBuilder().build());
            var memberCommunities = communityClient.listCommunitiesForMember(Empty.newBuilder().build());

            var allCommunities = Stream.concat(ownedCommunities.getCommunitiesList().stream(),
                    memberCommunities.getCommunitiesList().stream()).collect(Collectors.toList());

            Platform.runLater(() -> {
                communityComponent.replaceCommunities(allCommunities);

                var initialCommunity = allCommunities.get(0);
                if (initialCommunity != null) {
                    communityComponent.selectCommunityLink(initialCommunity);
                    communityComponent.setCommunityTitle(initialCommunity.getDisplayName());

                    communityComponent.replaceOwner(initialCommunity.getOwner());
                    communityComponent.replaceMemberList(initialCommunity.getMembersList());

                    var initialChannel = initialCommunity.getChannels(0);

                    if (initialChannel != null) {
                        communityComponent.setChannelTitle(initialChannel.getDisplayName());
                    }
                }
            });
        }).start();

        new Thread(() -> {
            var communityFilter = CommunityFilter.newBuilder().setCommunityId(1).build();

            var channels = communityClient.listChannelsForCommunity(communityFilter);

            Platform.runLater(() -> {
                communityComponent.setChannelList(channels.getChannelsList());
            });
        }).start();

        var scene = new Scene((Parent) communityComponent.render(), 1080, 720);

        primaryStage.setScene(scene);
        primaryStage.setTitle("unnecessary.ly");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}