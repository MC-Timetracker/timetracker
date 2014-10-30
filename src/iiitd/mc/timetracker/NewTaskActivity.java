package iiitd.mc.timetracker;

import iiitd.mc.timetracker.adapter.CustomArrayAdapter;
import iiitd.mc.timetracker.context.ITaskSuggestor;
import iiitd.mc.timetracker.context.MainTaskSuggestor;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;

public class NewTaskActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.activity_new_task);
		// use LayoutInflater in order to keep the NavigationDrawer of BaseActivity
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frame.addView(inflater.inflate(R.layout.activity_new_task, null));
		
		AutoCompleteTextView autoTv = (AutoCompleteTextView) findViewById(R.id.taskSelectionBox);
		
		ITaskSuggestor suggester = new MainTaskSuggestor();
		List<String> suggestedTasks = suggester.getTaskStrings();
		CustomArrayAdapter adapter = new CustomArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestedTasks);
		autoTv.setAdapter(adapter);
		
		autoTv.setThreshold(0);
		autoTv.setOnClickListener(new OnClickListener() {
				public void onClick(View view)
				{
					((AutoCompleteTextView)view).showDropDown();
				}
		});
		
	}
}
