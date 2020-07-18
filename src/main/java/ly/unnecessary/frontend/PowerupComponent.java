package ly.unnecessary.frontend;

import com.almasb.fxgl.entity.component.Component;

public class PowerupComponent extends Component {

    /*
     * PowerupType type;
     * 
     * static boolean isPowerupActive = false;
     * 
     * public void applyPowerup(PowerupType type) { System.out.println(type.name());
     * 
     * if (type == PowerupType.PLAYERGUN && !isPowerupActive) {
     * activatePlayergun(type); } }
     */

    private void activatePlayergun(PowerupType type) {
        /*
         * if (!isPowerupActive) { isPowerupActive = true; run(() -> { var player =
         * byType(EntityType.PLAYER).get(0); spawn("playergun", player.getX() +
         * player.getWidth() / 2, player.getY()); }, Duration.seconds(0.2f), 3);
         * isPowerupActive = false; }
         */
    }

    @Override
    public void onAdded() {
        // entity.getTransformComponent().setAnchoredPosition(entity.getCenter());
    }

    @Override
    public void onUpdate(double tpf) {

    }

    public void collide(int i) {

    }

}