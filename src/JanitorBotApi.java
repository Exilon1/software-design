import java.util.List;

public interface JanitorBotApi {

  Result transferToClean(String message, Result result);

  Result move(double forwardM, Result result);

  Result turn(double angle, Result result);

  Result set(State state, Result result);

  Result start(Result result);

  Result stop(Result result);

  Result make (String code, State state, Result result);
}
