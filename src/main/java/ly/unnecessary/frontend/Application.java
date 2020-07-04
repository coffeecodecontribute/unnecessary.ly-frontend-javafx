package ly.unnecessary.frontend;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import ly.unnecessary.backend.api.CommunityServiceGrpc;
import ly.unnecessary.backend.api.CommunityOuterClass.ChannelFilter;
import ly.unnecessary.backend.api.CommunityOuterClass.NewChat;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

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
        communityComponent.setOnCreateChat(chat -> {
            new Thread(() -> {
                var newChat = NewChat.newBuilder().setChannelId(1).setMessage(chat).build();

                communityClient.createChat(newChat);

                Platform.runLater(() -> communityComponent.clearAndFocusNewChatFieldText());
            }).start();

            return 0;
        });

        new Thread(() -> {
            var channelFilter = ChannelFilter.newBuilder().setChannelId(1).build();

            var stream = communityClient.subscribeToChannelChats(channelFilter);

            stream.forEachRemaining(c -> Platform.runLater(() -> communityComponent.addChat(c)));
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