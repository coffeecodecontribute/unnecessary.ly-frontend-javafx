package ly.unnecessary.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import ly.unnecessary.backend.api.CommunityOuterClass.Channel;
import ly.unnecessary.backend.api.CommunityOuterClass.Chat;
import ly.unnecessary.backend.api.CommunityOuterClass.Community;
import ly.unnecessary.backend.api.UserOuterClass.User;

public class CommunityComponent {
    private TextField newChatField;
    private VBox chatList;
    private VBox channel;
    private ScrollPane chatListWrapper;
    private VBox communities;
    private Map<Long, Button> communityIdToCommmunityLink = new HashMap<>();
    private HBox communityHeader;
    private HBox channelHeader;
    private ListView<Node> communityChannelsList;
    private Map<Integer, Channel> indexToChannelMap = new HashMap<>();
    private HBox invitePeopleWrapper;
    private Tooltip invitePeopleTooltip;
    private Button invitePeopleButton;
    private VBox ownerList;
    private VBox memberList;
    private User currentUser;
    private ScrollPane memberListWrapper;
    private VBox communityDetails;
    private User owner;
    private List<User> members;
    private Button addChannelButton;
    private Tooltip addChannelTooltip;
    private HBox addChannelButtonWrapper;

    private Consumer<String> onCreateChat;
    private Consumer<Community> onClickCommunityLink;
    private Consumer<Channel> onChannelClick;
    private Function<String, Boolean> onCreateChannel;
    private Function<String, Boolean> onCreateCommunity;
    private Function<String, Boolean> onJoinCommunity;
    private Supplier<String> onRequestInvite;

    public void setOnCreateChat(Consumer<String> onCreateChat) {
        this.onCreateChat = onCreateChat;
    }

    public void setOnSwitchCommunity(Consumer<Community> onClickCommunityLink) {
        this.onClickCommunityLink = onClickCommunityLink;
    }

    public void setOnSwitchChannel(Consumer<Channel> onChannelClick) {
        this.onChannelClick = onChannelClick;
    }

    public void setOnCreateChannel(Function<String, Boolean> onCreateChannel) {
        this.onCreateChannel = onCreateChannel;
    }

    public void setOnCreateCommunity(Function<String, Boolean> onCreateCommunity) {
        this.onCreateCommunity = onCreateCommunity;
    }

    public void setOnRequestInvite(Supplier<String> onRequestInvite) {
        this.onRequestInvite = onRequestInvite;
    }

    public void setOnJoinCommunity(Function<String, Boolean> onJoinCommunity) {
        this.onJoinCommunity = onJoinCommunity;
    }

    public void addChat(Chat chat) {
        this.chatList.getChildren()
                .add(this.createChat(channel.widthProperty(), this.getInitialsForUserId(chat.getUserId()),
                        chat.getMessage(), chat.getUserId() == this.currentUser.getId()));
    }

    public void setChats(List<Chat> chats) {
        this.chatList.getChildren()
                .setAll(chats.stream()
                        .map(c -> this.createChat(channel.widthProperty(), this.getInitialsForUserId(c.getUserId()),
                                c.getMessage(), c.getUserId() == this.currentUser.getId()))
                        .collect(Collectors.toList()));
    }

    public void clearAndFocusNewChatFieldText() {
        this.newChatField.clear();
        this.newChatField.requestFocus();
    }

    public void scrollChatsToBottom() {
        var animation = new Timeline(
                new KeyFrame(Duration.seconds(0.5), new KeyValue(this.chatListWrapper.vvalueProperty(), 1)));

        animation.play();
    }

    public void setCommunities(List<Community> communities) {
        this.communities.getChildren()
                .setAll(communities.stream().map(c -> this.createCommunityLink(c, false)).collect(Collectors.toList()));
    }

    public void setSelectedCommunity(Community community) {
        this.communityIdToCommmunityLink.values().stream().forEach(b -> b.setStyle(SIDEBAR_BUTTON_INACTIVE_STYLES));

        var buttonForCommunity = this.communityIdToCommmunityLink.get(community.getId());

        buttonForCommunity.setStyle(SIDEBAR_BUTTON_ACTIVE_STYLES);

        if (community.getOwner().getId() != this.currentUser.getId()) {
            this.addChannelButton.setDisable(true);
            this.addChannelTooltip = new Tooltip(String.format(
                    "Only the owner of this community (%s) can create channels in this community; please ask them to create a channel for you.",
                    this.owner.getDisplayName()));
            Tooltip.install(this.addChannelButtonWrapper, this.addChannelTooltip);

            this.invitePeopleButton.setDisable(true);
            this.invitePeopleTooltip = new Tooltip(String.format(
                    "Only the owner of this community (%s) can invite people to this community; please ask them to invite the latter for you.",
                    this.owner.getDisplayName()));
            Tooltip.install(this.invitePeopleWrapper, this.invitePeopleTooltip);
        } else {
            this.addChannelButton.setDisable(false);
            Tooltip.uninstall(this.addChannelButtonWrapper, this.addChannelTooltip);

            this.invitePeopleButton.setDisable(false);
            Tooltip.uninstall(this.invitePeopleButton, this.invitePeopleTooltip);
        }
    }

    public void setCommunityTitle(String communityTitle) {
        this.communityHeader.getChildren().setAll(this.createHeader(communityTitle));
    }

    public void setChannelTitle(String channelTitle) {
        this.channelHeader.getChildren().setAll(this.createHeader(channelTitle));
    }

    public void setChannels(List<Channel> channels) {
        for (var i = 0; i < channels.size(); i++) {
            this.indexToChannelMap.put(i, channels.get(i));
        }

        this.communityChannelsList.getItems()
                .setAll(channels.stream().map(c -> new Label(c.getDisplayName())).collect(Collectors.toList()));
    }

    public void setSelectedChannel(Channel channel) {
        var indexToSelect = this.indexToChannelMap.entrySet().stream().filter((e) -> e.getValue().equals(channel))
                .map((e) -> e.getKey()).findFirst();

        this.communityChannelsList.getSelectionModel().select(indexToSelect.get());
    }

    public void setOwner(User owner) {
        this.owner = owner;

        this.ownerList.getChildren().setAll(this.createHeader("Owner"),
                this.createUserPersona(this.getInitials(owner.getDisplayName()), owner.getDisplayName()));
    }

    public void setMembers(List<User> members) {
        this.members = members;

        var memberUserList = members.stream()
                .map(m -> this.createUserPersona(this.getInitials(m.getDisplayName()), m.getDisplayName()))
                .collect(Collectors.toList());

        var nodeList = new ArrayList<Node>();
        nodeList.add(this.createHeader("Members"));
        nodeList.addAll(memberUserList);
        nodeList.add(this.invitePeopleWrapper);

        this.memberList.getChildren().setAll(nodeList);
    }

    public void setCurrentUser(User newCurrentUser) {
        this.currentUser = newCurrentUser;

        var newAvatarHeader = this.createUserMenu(this.getInitials(this.currentUser.getDisplayName()),
                this.currentUser.getDisplayName());
        newAvatarHeader.setMaxWidth(Double.MAX_VALUE);

        this.communityDetails.getChildren().setAll(this.memberListWrapper, newAvatarHeader);
    }

    public Node render() {
        var wrapper = new BorderPane();

        // Community switcher
        var communitySwitcher = new VBox();

        var communityList = new ScrollPane();

        this.communities = new VBox();
        communities.setSpacing(8);
        communities.setPadding(new Insets(8, 0, 8, 8));

        communityList.setContent(communities);
        communityList.setVbarPolicy(ScrollBarPolicy.NEVER);
        communityList.setHbarPolicy(ScrollBarPolicy.NEVER);
        communityList.setStyle("-fx-background-color: transparent");

        VBox.setVgrow(communityList, Priority.ALWAYS);

        var communityAddButton = this.createCommunityAction(FontAwesomeSolid.PLUS, "Create community");
        communityAddButton.setOnAction((e) -> {
            var popoverContent = new VBox();
            popoverContent.setAlignment(Pos.CENTER_RIGHT);

            var popover = new PopOver(popoverContent);

            var nameField = new TextField();
            nameField.setPromptText("New community name");
            nameField.setOnAction((event) -> {
                if (this.onCreateCommunity.apply(nameField.getText())) {
                    popover.hide();
                }
            });

            var createButton = new Button("Create new community");
            createButton.setStyle("-fx-base: royalblue");
            createButton.setOnAction((event) -> {
                if (this.onCreateCommunity.apply(nameField.getText())) {
                    popover.hide();
                }
            });

            popoverContent.getChildren().setAll(nameField, createButton);
            popoverContent.setSpacing(8);
            popoverContent.setPadding(new Insets(8));

            popover.show(communityAddButton);
        });

        var communityJoinButton = this.createCommunityAction(FontAwesomeSolid.SIGN_IN_ALT, "Join community");
        communityJoinButton.setOnAction((e) -> {
            var popoverContent = new VBox();
            popoverContent.setAlignment(Pos.CENTER_RIGHT);

            var popover = new PopOver(popoverContent);

            var nameField = new TextField();
            nameField.setPromptText("Join token");
            nameField.setOnAction((event) -> {
                if (this.onJoinCommunity.apply(nameField.getText())) {
                    popover.hide();
                }
            });

            var createButton = new Button("Join community");
            createButton.setStyle("-fx-base: royalblue");
            createButton.setOnAction((event) -> {
                if (this.onJoinCommunity.apply(nameField.getText())) {
                    popover.hide();
                }
            });

            popoverContent.getChildren().setAll(nameField, createButton);
            popoverContent.setSpacing(8);
            popoverContent.setPadding(new Insets(8));

            popover.show(communityJoinButton);
        });

        var communityMainActions = new VBox(communityAddButton, communityJoinButton);
        communityMainActions.setSpacing(8);
        communityMainActions.setPadding(new Insets(8));

        communitySwitcher.getChildren().addAll(communityList, communityMainActions);

        // Community details
        this.communityDetails = new VBox();

        var avatarHeader = this.createUserMenu("", "");
        avatarHeader.setMaxWidth(Double.MAX_VALUE);

        this.memberListWrapper = new ScrollPane();

        var userList = new VBox();

        this.ownerList = new VBox();

        ownerList.getChildren().addAll(this.createHeader("Owner"));
        ownerList.setSpacing(8);
        ownerList.setPadding(new Insets(0, 0, 8, 0));

        this.memberList = new VBox();

        this.invitePeopleWrapper = new HBox();

        this.invitePeopleButton = this.createPrimaryAction(FontAwesomeSolid.USER_PLUS, "Invite people");
        this.invitePeopleButton.setOnAction((e) -> {
            var popoverContent = new VBox();
            popoverContent.setAlignment(Pos.CENTER);

            var popover = new PopOver(popoverContent);

            var inviteToken = this.onRequestInvite.get();

            var descriptionLabel = new Label("Share this token with the person you want to invite:");
            var tokenLabel = new Label(inviteToken);
            tokenLabel.setStyle("-fx-font-weight: bold");
            var copyToClipboardButton = new Button("Copy token to clipboard");
            copyToClipboardButton.setStyle("-fx-base: royalblue");
            copyToClipboardButton.setOnAction((event) -> {
                var clipboard = Clipboard.getSystemClipboard();

                var content = new ClipboardContent();
                content.putString(inviteToken);

                clipboard.setContent(content);
            });

            popoverContent.getChildren().setAll(descriptionLabel, tokenLabel, copyToClipboardButton);
            popoverContent.setSpacing(8);
            popoverContent.setPadding(new Insets(8));

            popover.show(this.invitePeopleButton);
        });
        HBox.setHgrow(this.invitePeopleButton, Priority.ALWAYS);
        this.invitePeopleWrapper.getChildren().add(this.invitePeopleButton);
        this.invitePeopleWrapper.setMaxWidth(Double.MAX_VALUE);

        memberList.getChildren().addAll(this.createHeader("Members"), this.invitePeopleWrapper);
        memberList.setSpacing(8);
        memberList.setPadding(new Insets(0, 0, 8, 0));
        memberList.setMaxWidth(Double.MAX_VALUE);

        userList.getChildren().addAll(ownerList, memberList);

        memberListWrapper.setVbarPolicy(ScrollBarPolicy.NEVER);
        memberListWrapper.setHbarPolicy(ScrollBarPolicy.NEVER);
        memberListWrapper.setStyle("-fx-background-color: transparent");
        memberListWrapper.setContent(userList);

        VBox.setVgrow(memberListWrapper, Priority.ALWAYS);

        communityDetails.getChildren().addAll(memberListWrapper, avatarHeader);
        communityDetails.setSpacing(8);
        communityDetails.setPadding(new Insets(8));

        // Community content
        var communityContent = new HBox();

        this.communityHeader = new HBox();
        communityHeader.getChildren().add(this.createHeader(""));

        var communityChannels = new VBox();

        this.communityChannelsList = new ListView<>();
        communityChannelsList.setMaxWidth(175);
        communityChannelsList.setOnMouseClicked((e) -> {
            var index = communityChannelsList.getSelectionModel().getSelectedIndex();

            if (index != -1) {
                this.onChannelClick.accept(this.indexToChannelMap.get(index));
            }
        });

        this.addChannelButtonWrapper = new HBox();
        this.addChannelButton = createPrimaryAction(FontAwesomeSolid.PLUS_SQUARE, "Create channel");
        addChannelButton.setOnAction((e) -> {
            var popoverContent = new VBox();
            popoverContent.setAlignment(Pos.CENTER_RIGHT);

            var popover = new PopOver(popoverContent);

            var nameField = new TextField();
            nameField.setPromptText("New channel name");
            nameField.setOnAction((event) -> {
                if (this.onCreateChannel.apply(nameField.getText())) {
                    popover.hide();
                }
            });

            var createButton = new Button("Create channel");
            createButton.setStyle("-fx-base: royalblue");
            createButton.setOnAction((event) -> {
                if (this.onCreateChannel.apply(nameField.getText())) {
                    popover.hide();
                }
            });

            popoverContent.getChildren().setAll(nameField, createButton);
            popoverContent.setSpacing(8);
            popoverContent.setPadding(new Insets(8));

            popover.show(addChannelButton);
        });
        addChannelButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(addChannelButton, Priority.ALWAYS);
        addChannelButtonWrapper.getChildren().add(addChannelButton);
        addChannelButtonWrapper.setAlignment(Pos.CENTER);
        addChannelButtonWrapper.setMinHeight(56);

        VBox.setVgrow(communityChannelsList, Priority.ALWAYS);

        communityChannels.getChildren().addAll(communityHeader, communityChannelsList, addChannelButtonWrapper);
        communityChannels.setSpacing(8);

        this.channel = new VBox();

        this.channelHeader = new HBox();

        this.chatListWrapper = new ScrollPane();

        chatListWrapper.setVbarPolicy(ScrollBarPolicy.NEVER);
        chatListWrapper.setHbarPolicy(ScrollBarPolicy.NEVER);
        chatListWrapper.setFitToWidth(true);

        this.chatList = new VBox();
        chatList.setSpacing(8);
        chatListWrapper.setContent(chatList);
        chatListWrapper.setHbarPolicy(ScrollBarPolicy.NEVER);
        chatListWrapper.setStyle("-fx-background-color: transparent");

        var newChatWrapper = new HBox();
        this.newChatField = new TextField();
        newChatField.setPromptText("New chat");
        newChatField.setPadding(new Insets(9));
        newChatField.setStyle("-fx-background-radius: 16 0 0 16");
        newChatField.setOnAction((e) -> this.onCreateChat.accept(newChatField.getText()));
        var sendChatButton = new Button();
        var sendIcon = new FontIcon(FontAwesomeSolid.PAPER_PLANE);
        sendIcon.setIconColor(Paint.valueOf("white"));
        sendChatButton.setGraphic(sendIcon);
        sendChatButton.setStyle("-fx-background-radius: 0 16 16 0; -fx-base: royalblue");
        sendChatButton.setPadding(new Insets(9, 14, 9, 9));
        sendChatButton.setOnAction((e) -> this.onCreateChat.accept(newChatField.getText()));
        HBox.setHgrow(newChatField, Priority.ALWAYS);
        newChatWrapper.getChildren().addAll(newChatField, sendChatButton);
        newChatWrapper.setAlignment(Pos.CENTER);
        newChatWrapper.setMinHeight(56);

        VBox.setVgrow(chatListWrapper, Priority.ALWAYS);

        channel.getChildren().addAll(channelHeader, chatListWrapper, newChatWrapper);
        channel.setSpacing(8);

        HBox.setHgrow(channel, Priority.ALWAYS);

        communityContent.getChildren().addAll(communityChannels, channel);
        communityContent.setSpacing(8);
        communityContent.setPadding(new Insets(8));

        wrapper.setLeft(communitySwitcher);
        wrapper.setCenter(communityContent);
        wrapper.setRight(communityDetails);

        Platform.runLater(() -> newChatField.requestFocus());

        wrapper.setStyle("-fx-font-family: 'Arial';");

        return wrapper;
    }

    private Button createCommunityLink(Community community, boolean active) {
        var shorthand = this.getInitials(community.getDisplayName());

        var link = new Button(shorthand);
        var tooltip = new Tooltip(community.getDisplayName());
        Tooltip.install(link, tooltip);

        if (active) {
            link.setStyle(SIDEBAR_BUTTON_ACTIVE_STYLES);
        } else {
            link.setStyle(SIDEBAR_BUTTON_INACTIVE_STYLES);
        }

        link.setOnAction((e) -> {
            this.onClickCommunityLink.accept(community);
        });

        this.communityIdToCommmunityLink.put(community.getId(), link);

        return link;
    }

    private Button createCommunityAction(Ikon iconName, String action) {
        var button = new Button();
        button.setGraphic(new FontIcon(iconName));
        var tooltip = new Tooltip(action);
        Tooltip.install(button, tooltip);

        button.setStyle("-fx-background-radius: 32; " + SIDEBAR_BUTTON_STYLES);

        return button;
    }

    private Button createPrimaryAction(Ikon iconName, String action) {
        var button = new Button();
        var innerAvatar = new HBox();
        var avatar = new FontIcon(iconName);
        var name = new Label(action);
        innerAvatar.setSpacing(8);
        innerAvatar.setPadding(new Insets(4));
        innerAvatar.getChildren().addAll(avatar, name);
        innerAvatar.setAlignment(Pos.CENTER);

        button.setGraphic(innerAvatar);
        button.setStyle("-fx-background-radius: 16;");

        return button;
    }

    private HBox createChat(ReadOnlyDoubleProperty width, String initials, String message, boolean fromSelf) {
        var chat = new HBox();

        var avatarPlaceholder = this.createProfilePicture(initials);

        var chatContent = new Text(message);

        chatContent.wrappingWidthProperty().bind(width.subtract(48));

        if (fromSelf) {
            chat.getChildren().addAll(chatContent, avatarPlaceholder);
            chatContent.setTextAlignment(TextAlignment.RIGHT);
        } else {
            chat.getChildren().addAll(avatarPlaceholder, chatContent);
        }

        chat.setSpacing(8);
        chat.setAlignment(Pos.CENTER);

        return chat;
    }

    private Button createUserMenu(String initials, String fullName) {
        var avatarHeader = new Button();
        var innerAvatar = this.createUserPersona(initials, fullName);

        avatarHeader.setGraphic(innerAvatar);
        avatarHeader.setStyle("-fx-background-radius: 16; -fx-min-height: 64; -fx-max-height: 64");

        return avatarHeader;
    }

    private HBox createUserPersona(String initials, String fullName) {
        var innerAvatar = new HBox();
        var avatar = this.createProfilePicture(initials);

        var name = new Label(fullName);
        innerAvatar.setSpacing(8);
        innerAvatar.setPadding(new Insets(4));
        innerAvatar.getChildren().addAll(avatar, name);
        innerAvatar.setAlignment(Pos.CENTER_LEFT);

        return innerAvatar;
    }

    private Label createProfilePicture(String initials) {
        var avatar = new Label(initials);
        avatar.setAlignment(Pos.CENTER);
        avatar.setShape(new Circle(8));
        avatar.setStyle(
                "-fx-background-color: black; -fx-text-fill: white; -fx-min-width: 32; -fx-min-height: 32; -fx-max-width: 32; -fx-max-height: 32; -fx-font-size: 10; -fx-font-weight: bold;");
        avatar.setPadding(new Insets(8));

        return avatar;
    }

    private Label createHeader(String title) {
        var ownerHeader = new Label(title);

        ownerHeader.setStyle("-fx-font-weight: bold;");

        return ownerHeader;
    }

    private String getInitials(String displayName) {
        var shorthand = displayName.substring(0, 2).toUpperCase();
        var initials = displayName.toUpperCase().split(" ");
        if (initials[1] != null)
            shorthand = initials[0].substring(0, 1) + initials[1].substring(0, 1);
        return shorthand;

    }

    private String getInitialsForUserId(long id) {
        if (this.currentUser.getId() == id) {
            return this.getInitials(this.currentUser.getDisplayName());
        }

        if (this.owner.getId() == id) {
            return this.getInitials(this.owner.getDisplayName());
        }

        var member = this.members.stream().filter(m -> m.getId() == id).findFirst();

        if (member.isPresent()) {
            return this.getInitials(member.get().getDisplayName());
        }

        return "-";
    }

    private static String SIDEBAR_BUTTON_STYLES = "-fx-min-width: 64; -fx-min-height: 64; -fx-max-width: 64; -fx-max-height: 64; -fx-font-size: 16; -fx-font-weight: bold;";
    private static String SIDEBAR_BUTTON_INACTIVE_STYLES = "-fx-background-radius: 32; " + SIDEBAR_BUTTON_STYLES;
    private static String SIDEBAR_BUTTON_ACTIVE_STYLES = "-fx-base: royalblue; -fx-background-radius: 16; "
            + SIDEBAR_BUTTON_STYLES;
}