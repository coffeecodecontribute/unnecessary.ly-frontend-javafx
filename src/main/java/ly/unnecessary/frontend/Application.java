package ly.unnecessary.frontend;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.protobuf.Empty;
import com.google.protobuf.InvalidProtocolBufferException;

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
import ly.unnecessary.backend.api.CommunityOuterClass.Invitation;
import ly.unnecessary.backend.api.CommunityOuterClass.InvitationCreateRequest;
import ly.unnecessary.backend.api.CommunityOuterClass.NewChannel;
import ly.unnecessary.backend.api.CommunityOuterClass.NewChat;
import ly.unnecessary.backend.api.CommunityOuterClass.NewCommunity;
import ly.unnecessary.backend.api.CommunityServiceGrpc;
import ly.unnecessary.backend.api.CommunityServiceGrpc.CommunityServiceBlockingStub;
import ly.unnecessary.backend.api.UserOuterClass.User;
import ly.unnecessary.backend.api.UserOuterClass.UserSignInRequest;
import ly.unnecessary.backend.api.UserServiceGrpc;
import ly.unnecessary.backend.api.UserServiceGrpc.UserServiceBlockingStub;
import ly.unnecessary.frontend.SignInComponent.SignInInfo;

public class Application extends javafx.application.Application {
    public static Metadata.Key<String> USER_EMAIL_KEY = Metadata.Key.of("x-uly-email", ASCII_STRING_MARSHALLER);
    public static Metadata.Key<String> USER_PASSWORD_KEY = Metadata.Key.of("x-uly-password", ASCII_STRING_MARSHALLER);

    private long currentChannelId = -1;
    private long currentCommunityId = -1;
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

            this.setCurrentCommunityId(communityToFetch.getId());

            var communityFilter = CommunityFilter.newBuilder().setCommunityId(communityToFetch.getId()).build();

            var newCommunity = this.communityClient.getCommunity(communityFilter);

            Platform.runLater(() -> {
                communityComponent.setCommunityTitle(newCommunity.getDisplayName());
                communityComponent.setOwner(newCommunity.getOwner());
                communityComponent.setMembers(newCommunity.getMembersList());
                communityComponent.setSelectedCommunity(newCommunity);
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

        Consumer<List<Community>> handleCommunitiesChange = (newCommunities) -> {
            Platform.runLater(() -> communityComponent.setCommunities(newCommunities));
        };

        Consumer<List<Community>> handleInit = (newCommunities) -> {
            handleCommunitiesChange.accept(newCommunities);

            if (newCommunities.size() == 0) {
                handleCommunitySwitch.accept(null);
            } else {
                handleCommunitySwitch.accept(newCommunities.get(0));
            }
        };

        Consumer<User> handleUserChange = (newUser) -> {
            Platform.runLater(() -> communityComponent.setCurrentUser(newUser));
        };

        Function<String, Boolean> handleCreateChannel = (newChannelName) -> {
            if (newChannelName.isEmpty()) {
                return false;
            }

            var newChannel = NewChannel.newBuilder().setCommunityId(this.getCurrentCommunityId())
                    .setDisplayName(newChannelName).build();

            this.communityClient.createChannel(newChannel);

            handleCommunitySwitch.accept(Community.newBuilder().setId(this.getCurrentCommunityId()).build());

            return true;
        };

        Supplier<List<Community>> handleCommunitiesRefresh = () -> {
            var ownedCommunities = this.communityClient.listCommunitiesForOwner(Empty.newBuilder().build());
            var memberCommunities = this.communityClient.listCommunitiesForMember(Empty.newBuilder().build());

            var allCommunities = Stream.concat(ownedCommunities.getCommunitiesList().stream(),
                    memberCommunities.getCommunitiesList().stream()).distinct().collect(Collectors.toList());

            return allCommunities;
        };

        Function<String, Boolean> handleCreateCommunity = (newCommunityName) -> {
            if (newCommunityName.isEmpty()) {
                return false;
            }

            var newCommunity = NewCommunity.newBuilder().setDisplayName(newCommunityName).build();

            var updatedCommunity = this.communityClient.createCommunity(newCommunity);

            var updatedCommunities = handleCommunitiesRefresh.get();
            handleCommunitiesChange.accept(updatedCommunities);

            handleCommunitySwitch.accept(updatedCommunity);

            return true;
        };

        Function<String, Boolean> handleJoinCommunity = (joinToken) -> {
            if (joinToken.isEmpty()) {
                return false;
            }

            var inviteAsBytes = Base64.getDecoder().decode(joinToken);

            final Invitation invite;
            try {
                invite = Invitation.parseFrom(inviteAsBytes);
            } catch (InvalidProtocolBufferException e) {
                return false;
            }

            final Community updatedCommunity;
            try {
                updatedCommunity = this.communityClient.acceptInvitation(invite);
            } catch (Exception e) {
                return false;
            }

            var updatedCommunities = handleCommunitiesRefresh.get();
            handleCommunitiesChange.accept(updatedCommunities);

            handleCommunitySwitch.accept(updatedCommunity);

            return true;
        };

        // Connect handlers
        communityComponent.setOnSwitchCommunity(handleCommunitySwitch);

        communityComponent.setOnSwitchChannel(handleChannelSwitch);

        communityComponent.setOnCreateChat(handleCreateChat);

        communityComponent.setOnCreateChannel(handleCreateChannel);

        communityComponent.setOnCreateCommunity(handleCreateCommunity);

        communityComponent.setOnRequestInvite(() -> {
            var invitationCreateRequest = InvitationCreateRequest.newBuilder()
                    .setCommunityId(this.getCurrentCommunityId()).build();

            var invite = this.communityClient.createInvitation(invitationCreateRequest);

            return Base64.getEncoder().encodeToString(invite.toByteArray());
        });

        communityComponent.setOnJoinCommunity(handleJoinCommunity);

        Function<SignInInfo, Integer> handleSignIn = (signInInfo) -> {
            // Validate connection details
            if (signInInfo.getApiUrl().isEmpty() || signInInfo.getEmail().isEmpty()
                    || signInInfo.getPassword().isEmpty()) {
                return 1;
            }

            // Setup connection
            var ch = ManagedChannelBuilder.forTarget(signInInfo.getApiUrl()).usePlaintext().build();

            // Sign in
            final User user;
            try {
                var signInClient = UserServiceGrpc.newBlockingStub(ch);
                user = signInClient.signIn(UserSignInRequest.newBuilder().setEmail(signInInfo.getEmail())
                        .setPassword(signInInfo.getPassword()).build());
            } catch (Exception e) {
                return 2;
            }

            handleUserChange.accept(user);

            // Setup authentication
            var metadata = new Metadata();
            metadata.put(USER_EMAIL_KEY, signInInfo.getEmail());
            metadata.put(USER_PASSWORD_KEY, signInInfo.getPassword());

            // Create clients
            this.userClient = MetadataUtils.attachHeaders(UserServiceGrpc.newBlockingStub(ch), metadata);
            this.communityClient = MetadataUtils.attachHeaders(CommunityServiceGrpc.newBlockingStub(ch), metadata);

            // Fetch owned communities
            var allCommunities = handleCommunitiesRefresh.get();

            handleInit.accept(allCommunities);

            // Render main component
            var scene = new Scene((Parent) communityComponent.render(), 1080, 720);

            primaryStage.setScene(scene);
            primaryStage.setTitle("unnecessary.ly");

            return 0;
        };

        Runnable handleStart = () -> {
            // Sign in
            var signInComponent = new SignInComponent();
            signInComponent.setOnSignIn(handleSignIn);

            // Render initial state
            var scene = new Scene((Parent) signInComponent.render(), 480, 320);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Sign in to unnecessary.ly");
        };

        communityComponent.setOnSignOut(handleStart);

        handleStart.run();

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

    public long getCurrentCommunityId() {
        return currentCommunityId;
    }

    public void setCurrentCommunityId(long currentCommunityId) {
        this.currentCommunityId = currentCommunityId;
    }
}