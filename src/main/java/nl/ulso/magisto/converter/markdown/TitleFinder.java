package nl.ulso.magisto.converter.markdown;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;

class TitleFinder {

  private String title = "";

  String extractTitle(Node rootNode) {
    NodeVisitor visitor = new NodeVisitor(new VisitHandler<>(Heading.class, this::visit));
    visitor.visit(rootNode);
    return title;
  }

  public void visit(Heading heading) {
    if (heading.getLevel() == 1) {
      title = heading.getText().toString();
    }
  }
}
