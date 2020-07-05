package ly.unnecessary.frontend;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import ly.unnecessary.backend.api.CommunityOuterClass.NewChat;
import ly.unnecessary.backend.api.CommunityServiceGrpc.CommunityServiceBlockingStub;
import ly.unnecessary.backend.api.UserOuterClass.User;
import ly.unnecessary.backend.api.UserOuterClass.UserSignInRequest;
import ly.unnecessary.backend.api.UserServiceGrpc.UserServiceBlockingStub;
import ly.unnecessary.backend.api.CommunityServiceGrpc;
import ly.unnecessary.backend.api.UserServiceGrpc;

public class Application extends javafx.application.Application {
    public static Metadata.Key<String> USER_EMAIL_KEY = Metadata.Key.of("x-uly-email", ASCII_STRING_MARSHALLER);
    public static Metadata.Key<String> USER_PASSWORD_KEY = Metadata.Key.of("x-uly-password", ASCII_STRING_MARSHALLER);

    private long currentChannelId = -1;
    private Map<Long, Boolean> chatListeners = new ConcurrentHashMap<>();
    private UserServiceBlockingStub userClient;
    private CommunityServiceBlockingStub communityClient;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create components
        var communityComponent = new CommunityComponent();

        // Create handlers
        Consumer<Channel> handleChannelSwitch = (newChannel) -> {
            if (newChannel == null) {
                this.setCurrentChannelId(-1);

                Platform.runLater(() -> {
                    communityComponent.setChannelTitle("");
                    communityComponent.setChats(List.of());
                });

                return;
            }

            this.setCurrentChannelId(newChannel.getId());

            Platform.runLater(() -> {
                communityComponent.setChannelTitle(newChannel.getDisplayName());
                communityComponent.setSelectedChannel(newChannel);
            });

            var channelFilter = ChannelFilter.newBuilder().setChannelId(newChannel.getId()).build();

            var newChats = this.communityClient.listChatsForChannel(channelFilter).getChatsList();

            Platform.runLater(() -> {
                communityComponent.setChats(newChats);
                communityComponent.scrollChatsToBottom();
            });

            var stream = this.communityClient.subscribeToChannelChats(channelFilter);

            new Thread(() -> {
                var listenerExists = false;

                try {
                    listenerExists = this.chatListeners.get(newChannel.getId());
                } catch (NullPointerException e) {

                }

                if (!listenerExists) {
                    this.chatListeners.put(newChannel.getId(), true);

                    stream.forEachRemaining(newChat -> {
                        if (newChat.getChannelId() == this.getCurrentChannelId()) {
                            Platform.runLater(() -> {
                                communityComponent.addChat(newChat);
                                communityComponent.scrollChatsToBottom();
                            });
                        }
                    });
                }
            }).start();
        };

        Consumer<Community> handleCommunitySwitch = (communityToFetch) -> {
            if (communityToFetch == null) {
                Platform.runLater(() -> {
                    communityComponent.setCommunityTitle("");
                    communityComponent.setOwner(User.newBuilder().setDisplayName("-").build());
                    communityComponent.setMembers(List.of(User.newBuilder().setDisplayName("-").build()));
                    communityComponent.setChannels(List.of());

                    handleChannelSwitch.accept(null);
                });

                return;
            }

            var communityFilter = CommunityFilter.newBuilder().setCommunityId(communityToFetch.getId()).build();

            var newCommunity = this.communityClient.getCommunity(communityFilter);

            Platform.runLater(() -> {
                communityComponent.setSelectedCommunity(newCommunity);
                communityComponent.setCommunityTitle(newCommunity.getDisplayName());
                communityComponent.setOwner(newCommunity.getOwner());
                communityComponent.setMembers(newCommunity.getMembersList());
            });

            var newChannels = this.communityClient.listChannelsForCommunity(communityFilter).getChannelsList();

            if (newChannels.size() == 0) {
                Platform.runLater(() -> communityComponent.setChannels(List.of()));

                handleChannelSwitch.accept(null);
            } else {
                Platform.runLater(() -> communityComponent.setChannels(newChannels));

                handleChannelSwitch.accept(newChannels.get(0));
            }
        };

        Consumer<String> handleCreateChat = (c) -> {
            if (!c.isEmpty()) {
                Platform.runLater(() -> communityComponent.clearAndFocusNewChatFieldText());

                var chat = NewChat.newBuilder().setChannelId(this.getCurrentChannelId()).setMessage(c).build();

                this.communityClient.createChat(chat);
            }
        };

        Consumer<List<Community>> handleInit = (newCommunities) -> {
            Platform.runLater(() -> {
                communityComponent.setCommunities(newCommunities);

                if (newCommunities.size() == 0) {
                    handleCommunitySwitch.accept(null);
                } else {
                    handleCommunitySwitch.accept(newCommunities.get(0));
                }
            });
        };

        Consumer<User> handleUserChange = (newUser) -> {
            Platform.runLater(() -> communityComponent.setCurrentUser(newUser));
        };

        // Connect handlers
        communityComponent.setOnSwitchCommunity(handleCommunitySwitch);

        communityComponent.setOnSwitchChannel(handleChannelSwitch);

        communityComponent.setOnCreateChat(handleCreateChat);

        // Set initial state
        new Thread(() -> {
            // Connection details
            var apiUrl = "localhost:1999";
            var email = "felix@pojtinger.com";
            var password = "pass1234";

            // Setup connection
            var ch = ManagedChannelBuilder.forTarget(apiUrl).usePlaintext().build();

            // Sign in
            var signInClient = UserServiceGrpc.newBlockingStub(ch);
            var user = signInClient
                    .signIn(UserSignInRequest.newBuilder().setEmail(email).setPassword(password).build());

            handleUserChange.accept(user);

            // Setup authentication
            var metadata = new Metadata();
            metadata.put(USER_EMAIL_KEY, email);
            metadata.put(USER_PASSWORD_KEY, password);

            // Create clients
            this.userClient = MetadataUtils.attachHeaders(UserServiceGrpc.newBlockingStub(ch), metadata);
            this.communityClient = MetadataUtils.attachHeaders(CommunityServiceGrpc.newBlockingStub(ch), metadata);

            // Fetch owned communities
            var ownedCommunities = this.communityClient.listCommunitiesForOwner(Empty.newBuilder().build());
            var memberCommunities = this.communityClient.listCommunitiesForMember(Empty.newBuilder().build());

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

    public long getCurrentChannelId() {
        return currentChannelId;
    }

    public void setCurrentChannelId(long currentChannelId) {
        this.currentChannelId = currentChannelId;
    }
}