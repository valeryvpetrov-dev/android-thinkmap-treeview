package com.gyso.gysotreeviewapplication;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gyso.gysotreeviewapplication.databinding.ActivityMainBinding;
import com.gyso.gysotreeviewapplication.model.AbstractScene;
import com.gyso.gysotreeviewapplication.model.Option;
import com.gyso.gysotreeviewapplication.model.Scene;
import com.gyso.gysotreeviewapplication.model.UnknownScene;
import com.gyso.treeview.TreeViewEditor;
import com.gyso.treeview.layout.CompactDownTreeLayoutManager;
import com.gyso.treeview.layout.TreeLayoutManager;
import com.gyso.treeview.line.BaseLine;
import com.gyso.treeview.line.StraightLine;
import com.gyso.treeview.listener.TreeViewControlListener;
import com.gyso.treeview.model.NodeModel;
import com.gyso.treeview.model.TreeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
	public static final String TAG = MainActivity.class.getSimpleName();
	private ActivityMainBinding binding;
	private Handler handler = new Handler();

	private TreeModel<AbstractScene> treeModel;
	private TreeViewEditor editor;
	private SceneTreeViewAdapter adapter;
	private NodeModel<AbstractScene> targetNode;
	private List<Scene> scenesAvailable = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		//demo init
		initWidgets();
	}

	/**
	 * To use a tree view, you should do 6 steps as follows:
	 * 1 customs adapter
	 * <p>
	 * 2 configure layout manager. Space unit is dp.
	 * You can custom you line by extends {@link BaseLine}
	 * <p>
	 * 3 view setting
	 * <p>
	 * 5 if you want to edit the map, then get and use and tree view editor
	 * <p>
	 * 6 you own others jobs
	 */
	private void initWidgets() {
		//1 customs adapter
		adapter = new SceneTreeViewAdapter();

		//2 configure layout manager; unit dp
		TreeLayoutManager treeLayoutManager = getTreeLayoutManager();

		//3 view setting
		binding.baseTreeView.setAdapter(adapter);
		binding.baseTreeView.setTreeLayoutManager(treeLayoutManager);

		//5 get an editor. Note: an adapter must set before get an editor.
		editor = binding.baseTreeView.getEditor();

		initData();

		//6 you own others jobs
		doYourOwnJobs(editor, adapter);
	}

	private void initData() {
		Scene scene1 = new Scene("Scene 1", new ArrayList<Option>() {
			{
				add(new Option("Option 1"));
			}
		});
		Scene scene2 = new Scene("Scene 2", new ArrayList<Option>() {
			{
				add(new Option("Option 1"));
				add(new Option("Option 2"));
			}
		});
		Scene scene3 = new Scene("Scene 3", new ArrayList<Option>() {
			{
				add(new Option("Option 1"));
			}
		});
		Scene scene4 = new Scene("Scene 4", new ArrayList<Option>() {
			{
				add(new Option("Option 1"));
			}
		});
		Scene scene5 = new Scene("Scene 5", new ArrayList<Option>());
		scenesAvailable.add(scene1);
		scenesAvailable.add(scene2);
		scenesAvailable.add(scene3);
		scenesAvailable.add(scene4);
		scenesAvailable.add(scene5);
	}

	void doYourOwnJobs(TreeViewEditor editor, SceneTreeViewAdapter adapter) {
		//drag to move node
		binding.dragEditModeRd.setOnCheckedChangeListener((v, isChecked) -> {
			editor.requestMoveNodeByDragging(isChecked);
		});

		//focus, means that tree view fill center in your window viewport
		binding.viewCenterBt.setOnClickListener(v -> editor.focusMidLocation());

		//add some nodes
		binding.addNodesBt.setOnClickListener(v -> {
			// TODO select node to add. Works only with "?" nodes
			showAvailableOptions();
		});

		//remove node
		binding.removeNodeBt.setOnClickListener(v -> {
			if (targetNode != null) editor.removeNode(targetNode);
		});

		adapter.setOnItemListener((item, node) -> {
			AbstractScene scene = node.getValue();
			Toast.makeText(this, String.format("Click on %s", scene.toString()), Toast.LENGTH_SHORT).show();
		});

		//treeView control listener
		final Object token = new Object();
		Runnable dismissRun = () -> {
			binding.scalePercent.setVisibility(View.GONE);
		};

		binding.baseTreeView.setTreeViewControlListener(new TreeViewControlListener() {
			@Override
			public void onScaling(int state, int percent) {
				Log.e(TAG, "onScaling: " + state + "  " + percent);
				binding.scalePercent.setVisibility(View.VISIBLE);
				if (state == TreeViewControlListener.MAX_SCALE) {
					binding.scalePercent.setText("MAX");
				} else if (state == TreeViewControlListener.MIN_SCALE) {
					binding.scalePercent.setText("MIN");
				} else {
					binding.scalePercent.setText(percent + "%");
				}
				handler.removeCallbacksAndMessages(token);
				handler.postAtTime(dismissRun, token, SystemClock.uptimeMillis() + 2000);
			}

			@Override
			public void onDragMoveNodesHit(NodeModel<?> draggingNode, NodeModel<?> hittingNode, View draggingView, View hittingView) {
				Log.e(TAG, "onDragMoveNodesHit: draging[" + draggingNode + "]hittingNode[" + hittingNode + "]");
			}
		});
	}

	private TreeLayoutManager getTreeLayoutManager() {
		int space_50dp = 30;
		int space_20dp = 20;
		BaseLine line = getLine();
		return new CompactDownTreeLayoutManager(this, space_50dp, space_20dp, line);
	}

	private BaseLine getLine() {
		return new StraightLine(Color.parseColor("#055287"), 2);
	}

	private void showAvailableOptions() {
		BottomSheetDialog addSceneDialog = new BottomSheetDialog(this);
		addSceneDialog.setContentView(R.layout.add_scene_layout);
		ListView scenesListView = addSceneDialog.findViewById(R.id.scenesList);
		Objects.requireNonNull(scenesListView);
		// Fill data
		String[] scenesIds = new String[scenesAvailable.size()];
		for (int i = 0; i < scenesIds.length; i++) scenesIds[i] = scenesAvailable.get(i).getId();
		ArrayAdapter<String> scenesAdapter = new ArrayAdapter<>(
				this,
				android.R.layout.simple_list_item_1,
				scenesIds
		);
		scenesListView.setAdapter(scenesAdapter);
		// Add listener
		scenesListView.setOnItemClickListener((adapterView, view, i, l) -> {
			Scene selectedScene = scenesAvailable.get((int) l);
			scenesAvailable.remove((int) l);
			addSceneDialog.dismiss();
			handler.post(() -> addScene(selectedScene));
		});
		addSceneDialog.show();
	}

	private void addScene(Scene selectedScene) {
		if (targetNode == null) {
			NodeModel<AbstractScene> root = new NodeModel<>(selectedScene);
			treeModel = new TreeModel<>(root);
			adapter.setTreeModel(treeModel);
			targetNode = root;
			addOptions(root);
		} else {
			NodeModel<AbstractScene> selectedSceneNode = new NodeModel<>(selectedScene);
			targetNode.setValue(selectedScene);
			addOptions(selectedSceneNode);
		}
	}

	@SuppressLint("DefaultLocale")
	private void addOptions(NodeModel<AbstractScene> sceneNode) {
		AbstractScene value = sceneNode.getValue();
		if (!(value instanceof Scene)) return;
		Scene scene = (Scene) value;
		NodeModel<?>[] optionNodes = new NodeModel<?>[scene.getOptions().size()];
		for (int i = 0; i < scene.getOptions().size(); i++) {
			optionNodes[i] = new NodeModel<>(new UnknownScene("Unknown"));
		}
		handler.postDelayed(() -> editor.addChildNodes(sceneNode, optionNodes), 1000);
	}
}