import java.util.Scanner;

public class BotStarter {

  public static void main(String[] args) {
    BootConfig bootConfig = new BootConfig();
    JanitorBotApi botApi = bootConfig.janitorBotApi();
    Result result = new Result(0, 0, 0, State.WATER);

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
          var distance = botApi.move(Double.parseDouble(parts[1]), result);
          botApi.transferToClean("POS " + distance.getCurrentX() + "," +
              distance.getCurrentY(), result);
        }
        case "turn" -> {
          if (parts.length != 2) {
            System.out.println("Usage: turn <angle>");
            continue;
          }
          botApi.transferToClean("ANGLE " + botApi.turn(Double.parseDouble(parts[1]), result),
              result);
        }
        case "set" -> {
          if (parts.length != 2) {
            System.out.println("Usage: set <state>");
            continue;
          }
          botApi.transferToClean("STATE " + botApi.set(State.valueOf(parts[1].toUpperCase()),
              result), result);
        }
        case "start" -> botApi.start(result);
        case "stop" -> {
          botApi.stop(result);
          return;
        }

        default -> System.out.println("Unknown command: " + command);
      }
    }
  }
}


class JanitorBot implements JanitorBotApi {

  @Override
  public Result transferToClean(String message, Result result) {
    System.out.println(message);
    return result;
  }

  @Override
  public Result move(double forwardM, Result result) {
    double radians = Math.toRadians(result.getCurrentAngle());
    result.setCurrentX(result.getCurrentX() + forwardM * Math.cos(radians));
    result.setCurrentY(result.getCurrentY() + forwardM * Math.sin(radians));

    return result;
  }

  @Override
  public Result turn(double angle, Result result) {
    result.setCurrentAngle(result.getCurrentAngle() + angle);
    return result;
  }

  @Override
  public Result set(State state, Result result) {
    result.setCurrentState(state);
    return result;
  }

  @Override
  public Result start(Result result) {
    transferToClean("START WITH " + result.getCurrentState(), result);
    return result;
  }

  @Override
  public Result stop(Result result) {
    transferToClean("STOP", result);
    return result;
  }

  @Override
  public Result make(String code, State state, Result result) {
    return result;
  }
}

class Result {

  private double currentX;
  private double currentY;
  private double currentAngle;
  private State currentState;

  public Result(double currentX, double currentY, double currentAngle, State currentState) {
    this.currentX = currentX;
    this.currentY = currentY;
    this.currentAngle = currentAngle;
    this.currentState = currentState;
  }

  public double getCurrentX() {
    return currentX;
  }

  public void setCurrentX(double currentX) {
    this.currentX = currentX;
  }

  public double getCurrentY() {
    return currentY;
  }

  public void setCurrentY(double currentY) {
    this.currentY = currentY;
  }

  public double getCurrentAngle() {
    return currentAngle;
  }

  public void setCurrentAngle(double currentAngle) {
    this.currentAngle = currentAngle;
  }

  public State getCurrentState() {
    return currentState;
  }

  public void setCurrentState(State currentState) {
    this.currentState = currentState;
  }
}

//Dependency injection
class BootConfig {

  public JanitorBotApi janitorBotApi() {
    return new JanitorBot();
  }
}

enum State {
  WATER,
  SOAP,
  BRUSH
}
