package tech.brettsaunders.craftory.tech.helpers;

public final class MathHelper {

  private MathHelper() {}

  public static int clamp(int a, int min, int max) {

    return a < min ? min : (a > max ? max : a);
  }

  public static float clamp(float a, float min, float max) {

    return a < min ? min : (a > max ? max : a);
  }

  public static double clamp(double a, double min, double max) {

    return a < min ? min : (a > max ? max : a);
  }

}
