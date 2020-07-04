package ly.unnecessary.frontend;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {
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

        VBox.setVgrow(communityList, Priority.ALWAYS);

        var communityAddButton = new Button("+");
        var communityJoinButton = new Button("j");

        var communityMainActions = new VBox(communityAddButton, communityJoinButton);

        communitySwitcher.getChildren().addAll(communityList, communityMainActions);

        // Community details
        var communityDetails = new VBox();

        var avatarHeader = new Button("FP Felix Pojtinger");

        var memberListWrapper = new ScrollPane();
        var memberList = new VBox();

        memberList.getChildren().addAll(new Label("Owner"), avatarHeader, new Label("Members"), this.createHeader(),
                this.createHeader(), this.createHeader());
        memberListWrapper.setContent(memberList);

        VBox.setVgrow(memberListWrapper, Priority.ALWAYS);

        var invitePeopleButton = new Button("+ Invite People");

        communityDetails.getChildren().addAll(avatarHeader, memberListWrapper, invitePeopleButton);

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

        var scene = new Scene(wrapper, 1080, 720);

        primaryStage.setScene(scene);
        primaryStage.setTitle("unnecessary.ly");

        primaryStage.show();
    }

    private HBox createHeader() {
        var avatarHeader = new HBox();

        var avatarPlaceholder = new Label("FP");

        var avatarName = new Label("Felix Pojtinger");

        avatarHeader.getChildren().addAll(avatarPlaceholder, avatarName);

        return avatarHeader;
    }

    private Button createCommunityLink(String initials, boolean active, String fullName) {
        var link = new Button(initials);
        var tooltip = new Tooltip(fullName);
        Tooltip.install(link, tooltip);

        var baseStyles = "-fx-min-width: 64; -fx-min-height: 64; -fx-max-width: 64; -fx-max-height: 64; -fx-font-size: 16;";

        if (active) {
            link.setStyle("-fx-base: royalblue; -fx-background-radius: 16; " + baseStyles);
        } else {
            link.setStyle("-fx-background-radius: 32; " + baseStyles);
        }

        return link;
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

    public static void main(String[] args) {
        launch(args);
    }
}