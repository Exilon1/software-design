import java.util.List;
import java.util.Scanner;

public class BotStarter {

  public static void main(String[] args) {
    JanitorBot bot = new JanitorBot(
        0, 0, 0, State.WATER
    );

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
            System.out.println("Usage: move <distance>");
            continue;
          }
          var distance = bot.move(Double.parseDouble(parts[1]));
          bot.transferToClean("POS " + distance.get(0) + "," + distance.get(1));
        }
        case "turn" -> {
          if (parts.length != 2) {
            System.out.println("Usage: turn <angle>");
            continue;
          }
          bot.transferToClean("ANGLE " + bot.turn(Double.parseDouble(parts[1])));
        }
        case "set" -> {
          if (parts.length != 2) {
            System.out.println("Usage: set <state>");
            continue;
          }
          bot.transferToClean("STATE " + bot.set(State.valueOf(parts[1].toUpperCase())));
        }
        case "start" -> bot.start();
        case "stop" -> {
          bot.stop();
          return;
        }

        default -> System.out.println("Unknown command: " + command);
      }
    }
  }
}


class JanitorBot implements JanitorBotApi {

  private double currentX;
  private double currentY;
  private double currentAngle;
  private State currentState;

  public JanitorBot(double currentX, double currentY, double currentAngle, State currentState) {
    this.currentX = currentX;
    this.currentY = currentY;
    this.currentAngle = currentAngle;
    this.currentState = currentState;
  }

  @Override
  public void transferToClean(String message) {
    System.out.println(message);
  }

  @Override
  public List<Double> move(double forwardM) {
    double radians = Math.toRadians(currentAngle);
    currentX = currentX + forwardM * Math.cos(radians);
    currentY = currentY + forwardM * Math.sin(radians);

    return List.of(
        currentX,
        currentY
    );
  }

  @Override
  public double turn(double angle) {
    currentAngle += angle;
    return currentAngle;
  }

  @Override
  public State set(State state) {
    currentState = state;
    return currentState;
  }

  @Override
  public State start() {
    transferToClean("START WITH " + currentState);
    return currentState;
  }

  @Override
  public void stop() {
    transferToClean("STOP");
  }

  @Override
  public void make(String code, State state) {

  }
}

enum State {
  WATER,
  SOAP,
  BRUSH
}
