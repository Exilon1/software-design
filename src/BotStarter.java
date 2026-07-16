import java.util.Map;
import java.util.Scanner;

public class BotStarter {

  public static void main(String[] args) {
    BootConfig bootConfig = new BootConfig();
    JanitorBotApi botApi = bootConfig.janitorBotApi();
    Result result = new Result(0, 0, 0, State.WATER);

    Map<String, RobotProgram> program = Map.of(
        "move", botApi::move,
        "turn", botApi::turn,
        "set", botApi::set,
        "start", botApi::start,
        "stop", botApi::stop
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
          var distance = program.get("move").run(new Command(Double.parseDouble(parts[1]), 0), result);
          botApi.transferToClean("POS " + distance.getCurrentX() + "," +
              distance.getCurrentY(), result);
        }
        case "turn" -> {
          if (parts.length != 2) {
            System.out.println("Usage: turn <angle>");
            continue;
          }
          botApi.transferToClean("ANGLE " + program.get("turn").run(new Command(0, Double.parseDouble(parts[1])), result),
              result);
        }
        case "set" -> {
          if (parts.length != 2) {
            System.out.println("Usage: set <state>");
            continue;
          }
          botApi.transferToClean("STATE " + program.get("set").run(new Command(State.valueOf(parts[1].toUpperCase())),
              result), result);
        }
        case "start" -> program.get("start").run(new Command(), result);
        case "stop" -> {
          program.get("stop").run(new Command(), result);
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
  public Result move(Command command, Result result) {
    double radians = Math.toRadians(result.getCurrentAngle());
    result.setCurrentX(result.getCurrentX() + command.getForwardM() * Math.cos(radians));
    result.setCurrentY(result.getCurrentY() + command.getForwardM() * Math.sin(radians));

    return result;
  }

  @Override
  public Result turn(Command command, Result result) {
    result.setCurrentAngle(result.getCurrentAngle() + command.getAngle());
    return result;
  }

  @Override
  public Result set(Command command, Result result) {
    result.setCurrentState(command.getState());
    return result;
  }

  @Override
  public Result start(Command command, Result result) {
    transferToClean("START WITH " + result.getCurrentState(), result);
    return result;
  }

  @Override
  public Result stop(Command command, Result result) {
    transferToClean("STOP", result);
    return result;
  }

  @Override
  public Result make(Command command, Result result) {
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

class Command {
  private double forwardM;
  private double angle;
  private State state;
  private String code;

  public Command() {
  }

  public Command(double forwardM, double angle) {
    this.forwardM = forwardM;
    this.angle = angle;
  }

  public Command(State state) {
    this.state = state;
  }

  public Command(String code, State state) {
    this.code = code;
    this.state = state;
  }

  public double getForwardM() {
    return forwardM;
  }

  public double getAngle() {
    return angle;
  }

  public State getState() {
    return state;
  }

  public String getCode() {
    return code;
  }
}

//Dependency injection
class BootConfig {

  public JanitorBotApi janitorBotApi() {
    return new JanitorBot();
  }
}

@FunctionalInterface
interface RobotProgram {
  Result run(Command command, Result result);
}

enum State {
  WATER,
  SOAP,
  BRUSH
}
