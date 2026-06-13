import java.util.List;
import java.util.Scanner;

public class JanitorBot {

  private static double currentX;
  private static double currentY;
  private static double currentAngle;
  private static State currentState;

  public static void main(String[] args) {
    currentX = 0;
    currentY = 0;
    currentAngle = 0;
    currentState = State.WATER;

    Scanner scanner = new Scanner(System.in);

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine().trim();

      if (line.isEmpty()) {
        continue;
      }
      String[] parts = line.split("\\s+", 2);

      String command = parts[0];

      switch (command) {
        case "move" -> {
          if (parts.length != 2) {
            transferToClean("Usage: move <distance>");
            continue;
          }
          var distance = move(Double.parseDouble(parts[1]));
          transferToClean("POS " + distance.get(0) + "," + distance.get(1));
        }
        case "turn" -> {
          if (parts.length != 2) {
            transferToClean("Usage: turn <angle>");
            continue;
          }
          transferToClean("ANGLE " + turn(Double.parseDouble(parts[1])));
        }
        case "set" -> {
          if (parts.length != 2) {
            System.out.println("Usage: set <state>");
            continue;
          }
          transferToClean("STATE " + set(State.valueOf(parts[1].toUpperCase())));
        }
        case "start" -> transferToClean("START WITH " + currentState);
        case "stop" -> {
          stop();
          transferToClean("STOP");
          return;
        }

        default -> transferToClean("Unknown command: " + command);
      }
    }
  }

  public static void transferToClean(String message) {
    System.out.println(message);
  }

  public static List<Double> move(double forwardM) {
    double radians = Math.toRadians(currentAngle);
    currentX = currentX + forwardM * Math.cos(radians);
    currentY = currentY + forwardM * Math.sin(radians);

    return List.of(
        currentX,
        currentY
    );
  }

  public static double turn(double angle) {
    currentAngle = currentAngle + angle;
    return currentAngle;
  }

  public static State set(State state) {
    currentState = state;
    return currentState;
  }

  public static State start() {
    return currentState;
  }

  public static void stop() {

  }
}

enum State {
  WATER,
  SOAP,
  BRUSH
}
