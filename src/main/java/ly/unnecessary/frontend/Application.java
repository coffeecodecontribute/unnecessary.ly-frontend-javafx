package ly.unnecessary.frontend;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        var wrapper = new BorderPane();

        // Community switcher
        var communitySwitcher = new VBox();

        var communityList = new ScrollPane();

        var communities = new VBox();
        var community1 = new Button("1");
        var community2 = new Button("2");
        var community3 = new Button("3");
        communities.getChildren().addAll(community1, community2, community3);

        communityList.setContent(communities);

        VBox.setVgrow(communityList, Priority.ALWAYS);

        var communityAddButton = new Button("+");
        var communityJoinButton = new Button("j");

        var communityMainActions = new VBox(communityAddButton, communityJoinButton);

        communitySwitcher.getChildren().addAll(communityList, communityMainActions);

        // Community details
        var communityDetails = new VBox();

        var avatarHeader = this.createHeader();

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

        communityContent.getChildren().addAll(communityChannels);

        wrapper.setLeft(communitySwitcher);
        wrapper.setCenter(communityContent);
        wrapper.setRight(communityDetails);

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

    public static void main(String[] args) {
        launch(args);
    }
}