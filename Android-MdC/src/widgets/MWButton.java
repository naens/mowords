package widgets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RemoteViews;

import com.naens.android_mdc.R;

public class MWButton extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		RemoteViews remoteViews;
		ComponentName widget;
		DateFormat format = SimpleDateFormat.getTimeInstance(
				SimpleDateFormat.MEDIUM, Locale.getDefault());

		remoteViews = new RemoteViews(context.getPackageName(), R.layout.mw_button);
		widget = new ComponentName(context, MWButton.class);
		remoteViews.setTextViewText(R.id.mw_button_layout, "Time = " + format.format(new Date()));
		appWidgetManager.updateAppWidget(widget, remoteViews);
	}

	public MWButton() {
	}

	public MWButton(Context context, AttributeSet attributeSet) {
		super();
	}

}
