package tech.brettsaunders.craftory.packet_wrapper;

import lombok.Getter;

public class Angle {

  @Getter
  int angle = 0;

  public void setAngle(int angle) {
    if (angle < 0) {
      this.angle = 0;
    } else if (angle > 360) {
      this.angle = 360;
    } else {
      this.angle = angle;
    }
  }

  //can't handle amount greater than 360
  public void add(int amount) {
    int result = this.angle + amount;
    if (result > 360) {
      this.angle = result % 360;
    } else if (result < 0) {
      this.angle = 360 + result;
    } else {
      this.angle = result;
    }
  }
}
