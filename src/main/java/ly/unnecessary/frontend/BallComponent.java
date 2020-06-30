package ly.unnecessary.frontend;


import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BallComponent extends Component {

    int ballSpeedMult = 5;
    int xSpeed = 1;
    int ySpeed = 4;
    int radius = 24;

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(entity.getCenter());
    }

    @Override
    public void onUpdate(double tpf) {
        //collide();
        //System.out.println("Ball X: " + entity.getX() + " | Ball Y: " + entity.getY());

        entity.setX(entity.getX() + this.xSpeed * ballSpeedMult);
        entity.setY(entity.getY() - this.ySpeed * ballSpeedMult);
    }

    public void collide(int i) {
        if(i == 0)
            this.xSpeed = this.xSpeed * -1;
        else if(i == 1)
            this.ySpeed = this.ySpeed * -1;
    }

    public void collide() {
        // wall collision
        if(entity.getX() > getAppWidth() - 2 * radius || entity.getX() < 0) {
            this.xSpeed = this.xSpeed * -1;
        } else if(entity.getY() < 0 || entity.getY() > getAppHeight() - 2 * radius) {
            this.ySpeed = this.ySpeed * -1;
        }
    }


    public void collideBlock() {
        this.ySpeed = this.ySpeed * -1;
    }
}
