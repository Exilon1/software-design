import java.util.List;

public interface JanitorBotApi {

  Result transferToClean(String message, Result result);

  Result move(Command command, Result result);

  Result turn(Command command, Result result);

  Result set(Command command, Result result);

  Result start(Command command, Result result);

  Result stop(Command command,Result result);

  Result make (Command command, Result result);
}
