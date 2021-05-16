package com.ververica.platform.sql.functions;

import com.ververica.platform.PatternUtils;
import java.util.regex.Matcher;
import org.apache.flink.table.functions.TableFunction;

/**
 * Table SQL function to extract the Jira components of a ticket created/change email from the text
 * body field sent by Jira.
 *
 * @see GetJiraTicketComponents for a Scalar function version of this
 */
@SuppressWarnings("unused")
public class ExpandJiraTicketComponents2
    extends TableFunction<ExpandJiraTicketComponents2.Component> {

  public static class Component {
    public String component;
    public Integer componentCount;

    public Component(String component, Integer componentCount) {
      this.component = component;
      this.componentCount = componentCount;
    }
  }

  public void eval(String textBody) {
    if (textBody != null) {
      Matcher matcher = PatternUtils.EMAIL_BODY_JIRA_TICKET_COMPONENTS_PATTERN.matcher(textBody);
      if (matcher.find()) {
        String[] components =
            PatternUtils.EMAIL_BODY_JIRA_TICKET_COMPONENTS_SPLIT_PATTERN.split(
                matcher.group("components"));
        for (String component : components) {
          collect(new Component(component, components.length));
        }
      }
    }
  }
}
