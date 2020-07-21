package ly.unnecessary.frontend;

import com.almasb.fxgl.entity.component.Component;

public class Player extends Component{

    //variables
    private final int y = 3;
    private int x;
    private boolean gun;
    private int height;
    private int width;
    
    //constuctor
    public Player(int x, boolean gun, int height, int width) {
        this.x = x;
        this.gun = gun;
        this.height = height;
        this.width = width;
    }

    //methods
    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public boolean isGun() {
        return gun;
    }

    public void setGun(boolean gun) {
        this.gun = gun;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}