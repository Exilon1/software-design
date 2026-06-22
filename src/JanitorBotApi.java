import java.util.List;

public interface JanitorBotApi {

  void transferToClean(String message);

  List<Double> move(double forwardM);

  double turn(double angle);

  State set(State state);

  State start();

  void stop();

  void make (String code, State state);
}
