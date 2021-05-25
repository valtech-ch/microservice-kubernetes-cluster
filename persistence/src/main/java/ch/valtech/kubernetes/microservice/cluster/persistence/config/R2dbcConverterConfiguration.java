package ch.valtech.kubernetes.microservice.cluster.persistence.config;

import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Action;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;

@Configuration
public class R2dbcConverterConfiguration {

  @WritingConverter
  class ActionWritingConverter implements Converter<Action, Integer> {

    @Override
    public Integer convert(Action action) {
      return action.ordinal();
    }

  }

  @ReadingConverter
  class ActionReadingConverter implements Converter<Integer, Action> {

    @Override
    public Action convert(Integer ordinal) {
      return Action.values()[ordinal];
    }

  }

  @Bean
  public R2dbcCustomConversions customConversions() {
    return new R2dbcCustomConversions(List.of(new ActionWritingConverter(), new ActionReadingConverter()));
  }

}
