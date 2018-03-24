// Generated code from Butter Knife. Do not modify!
package mp3ready.ui;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends mp3ready.ui.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624083, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131624083, "field 'toolbar'");
    view = finder.findRequiredView(source, 2131624085, "field 'fab'");
    target.fab = finder.castView(view, 2131624085, "field 'fab'");
    view = finder.findRequiredView(source, 2131624086, "field 'fab2'");
    target.fab2 = finder.castView(view, 2131624086, "field 'fab2'");
    view = finder.findRequiredView(source, 2131624081, "field 'drawer'");
    target.drawer = finder.castView(view, 2131624081, "field 'drawer'");
    view = finder.findRequiredView(source, 2131624084, "field 'playerView'");
    target.playerView = finder.castView(view, 2131624084, "field 'playerView'");
    view = finder.findRequiredView(source, 2131624082, "field 'navigationView'");
    target.navigationView = finder.castView(view, 2131624082, "field 'navigationView'");
  }

  @Override public void unbind(T target) {
    target.toolbar = null;
    target.fab = null;
    target.fab2 = null;
    target.drawer = null;
    target.playerView = null;
    target.navigationView = null;
  }
}
