package ly.unnecessary.frontend;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {

    private static String SIDEBAR_BUTTON_STYLES = "-fx-min-width: 64; -fx-min-height: 64; -fx-max-width: 64; -fx-max-height: 64; -fx-font-size: 16; -fx-font-weight: bold;";

    @Override
    public void start(Stage primaryStage) throws Exception {
        var wrapper = new BorderPane();

        // Community switcher
        var communitySwitcher = new VBox();

        var communityList = new ScrollPane();

        var communities = new VBox();
        var community1 = this.createCommunityLink("FP", true, "Felix Pojtinger's Community");
        var community2 = this.createCommunityLink("AD", false, "Alice Duck's Community");
        var community3 = this.createCommunityLink("BO", false, "Bob Oliver's Communitiy");
        communities.getChildren().addAll(community1, community2, community3);
        communities.setSpacing(8);
        communities.setPadding(new Insets(8));

        communityList.setContent(communities);
        communityList.setVbarPolicy(ScrollBarPolicy.NEVER);
        communityList.setHbarPolicy(ScrollBarPolicy.NEVER);
        communityList.setStyle("-fx-background-color: transparent");

        VBox.setVgrow(communityList, Priority.ALWAYS);

        var communityAddButton = this.createCommunityAction(FontAwesomeSolid.PLUS, "Create community");
        var communityJoinButton = this.createCommunityAction(FontAwesomeSolid.DOOR_OPEN, "Join community");

        var communityMainActions = new VBox(communityAddButton, communityJoinButton);
        communityMainActions.setSpacing(8);
        communityMainActions.setPadding(new Insets(8));

        communitySwitcher.getChildren().addAll(communityList, communityMainActions);

        // Community details
        var communityDetails = new VBox();

        var avatarHeader = this.createUserMenu("FP", "Felix Pojtinger");

        var memberListWrapper = new ScrollPane();

        var userList = new VBox();

        var ownerList = new VBox();

        ownerList.getChildren().addAll(this.createHeader("Owner"), this.createUserPersona("FP", "Felix Pojtinger"));
        ownerList.setSpacing(8);
        ownerList.setPadding(new Insets(0, 0, 8, 0));

        var memberList = new VBox();

        memberList.getChildren().addAll(this.createHeader("Members"), this.createUserPersona("AD", "Alice Duck"),
                this.createUserPersona("BO", "Bob Oliver"), this.createUserPersona("PK", "Peter Kropotkin"));
        memberList.setSpacing(8);
        memberList.setPadding(new Insets(0, 0, 8, 0));

        userList.getChildren().addAll(ownerList, memberList);

        memberListWrapper.setVbarPolicy(ScrollBarPolicy.NEVER);
        memberListWrapper.setHbarPolicy(ScrollBarPolicy.NEVER);
        memberListWrapper.setStyle("-fx-background-color: transparent");
        memberListWrapper.setContent(userList);

        VBox.setVgrow(memberListWrapper, Priority.ALWAYS);

        var invitePeopleButton = new Button("+ Invite People");

        communityDetails.getChildren().addAll(avatarHeader, memberListWrapper, invitePeopleButton);
        communityDetails.setSpacing(8);
        communityDetails.setPadding(new Insets(8));

        // Community content
        var communityContent = new HBox();

        var communityHeader = new HBox();
        communityHeader.getChildren().add(new Label("Community 1"));

        var communityChannels = new VBox();

        var communityChannelsList = new ListView<>();
        communityChannelsList.getItems().addAll(new Label("Channel 1"), new Label("Channel 2"), new Label("Channel 3"));

        var addChannelButton = new Button("+ Add Channel");

        VBox.setVgrow(communityChannelsList, Priority.ALWAYS);

        communityChannels.getChildren().addAll(communityHeader, communityChannelsList, addChannelButton);

        var channel = new VBox();

        var channelHeader = new HBox();
        channelHeader.getChildren().add(new Label("Channel 1"));

        var chatListWrapper = new ScrollPane();

        chatListWrapper.setHbarPolicy(ScrollBarPolicy.NEVER);
        chatListWrapper.setFitToWidth(true);

        var chatList = new VBox();

        chatList.getChildren().addAll(this.createChat(channel.widthProperty()),
                this.createChat(channel.widthProperty()), this.createChat(channel.widthProperty()));
        chatListWrapper.setContent(chatList);

        var newChatBox = new TextField();
        newChatBox.setPromptText("New chat");

        VBox.setVgrow(chatListWrapper, Priority.ALWAYS);

        channel.getChildren().addAll(channelHeader, chatListWrapper, newChatBox);

        HBox.setHgrow(channel, Priority.ALWAYS);

        communityContent.getChildren().addAll(communityChannels, channel);

        wrapper.setLeft(communitySwitcher);
        wrapper.setCenter(communityContent);
        wrapper.setRight(communityDetails);

        Platform.runLater(() -> newChatBox.requestFocus());

        wrapper.setStyle("-fx-font-family: 'Arial';");

        var scene = new Scene(wrapper, 1080, 720);

        primaryStage.setScene(scene);
        primaryStage.setTitle("unnecessary.ly");

        primaryStage.show();
    }

    private Button createCommunityLink(String initials, boolean active, String fullName) {
        var link = new Button(initials);
        var tooltip = new Tooltip(fullName);
        Tooltip.install(link, tooltip);

        if (active) {
            link.setStyle("-fx-base: royalblue; -fx-background-radius: 16; " + SIDEBAR_BUTTON_STYLES);
        } else {
            link.setStyle("-fx-background-radius: 32; " + SIDEBAR_BUTTON_STYLES);
        }

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

    private HBox createChat(ReadOnlyDoubleProperty width) {
        var chat = new HBox();

        var avatarPlaceholder = new Label("FP");

        var chatContent = new Text(
                "Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        chatContent.wrappingWidthProperty().bind(width.subtract(20));

        chat.getChildren().addAll(avatarPlaceholder, chatContent);

        return chat;
    }

    private Button createUserMenu(String initials, String fullName) {
        var avatarHeader = new Button();
        var innerAvatar = this.createUserPersona(initials, fullName);

        avatarHeader.setGraphic(innerAvatar);
        avatarHeader.setStyle("-fx-background-radius: 24;");

        return avatarHeader;
    }

    private HBox createUserPersona(String initials, String fullName) {
        var innerAvatar = new HBox();
        var avatar = new Label(initials);
        avatar.setAlignment(Pos.CENTER);
        avatar.setShape(new Circle(8));
        avatar.setStyle(
                "-fx-background-color: black; -fx-text-fill: white; -fx-min-width: 32; -fx-min-height: 32; -fx-max-width: 32; -fx-max-height: 32; -fx-font-size: 10; -fx-font-weight: bold;");
        avatar.setPadding(new Insets(8));
        var name = new Label(fullName);
        innerAvatar.setSpacing(8);
        innerAvatar.setPadding(new Insets(4));
        innerAvatar.getChildren().addAll(avatar, name);
        innerAvatar.setAlignment(Pos.CENTER_LEFT);

        return innerAvatar;
    }

    private Label createHeader(String title) {
        var ownerHeader = new Label(title);

        ownerHeader.setStyle("-fx-font-weight: bold;");

        return ownerHeader;
    }

    public static void main(String[] args) {
        launch(args);
    }
}