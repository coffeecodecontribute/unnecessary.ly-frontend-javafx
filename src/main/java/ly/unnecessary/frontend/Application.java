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

        var communityDetails = new VBox();

        var communityContent = new HBox();

        wrapper.setLeft(communitySwitcher);
        wrapper.setCenter(communityContent);
        wrapper.setRight(communityDetails);

        var scene = new Scene(wrapper, 1080, 720);

        primaryStage.setScene(scene);
        primaryStage.setTitle("unnecessary.ly");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}