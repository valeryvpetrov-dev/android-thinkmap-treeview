package com.gyso.gysotreeviewapplication.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gyso.gysotreeviewapplication.R;
import com.gyso.gysotreeviewapplication.databinding.NodeBaseLayoutBinding;
import com.gyso.treeview.adapter.DrawInfo;
import com.gyso.treeview.adapter.TreeViewAdapter;
import com.gyso.treeview.adapter.TreeViewHolder;
import com.gyso.treeview.line.BaseLine;
import com.gyso.treeview.model.NodeModel;

public class SceneTreeViewAdapter extends TreeViewAdapter<Scene> {
	private OnItemClickListener listener;

	public void setOnItemListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	@Override
	public TreeViewHolder<Scene> onCreateViewHolder(@NonNull ViewGroup viewGroup, NodeModel<Scene> node) {
		NodeBaseLayoutBinding nodeBinding = NodeBaseLayoutBinding.inflate(
				LayoutInflater.from(viewGroup.getContext()),
				viewGroup,
				false
		);
		return new TreeViewHolder<>(nodeBinding.getRoot(), node);
	}

	@Override
	public void onBindViewHolder(@NonNull TreeViewHolder<Scene> holder) {
		View itemView = holder.getView();
		NodeModel<Scene> node = holder.getNode();
		TextView idTextView = itemView.findViewById(R.id.id);
		final Scene animal = node.value;
		idTextView.setText(animal.id);
		itemView.setOnClickListener(v -> {
			if (listener != null) listener.onItemClick(v, node);
		});
	}

	@Override
	public BaseLine onDrawLine(DrawInfo drawInfo) {
		return null;
	}

	public interface OnItemClickListener {
		void onItemClick(View item, NodeModel<Scene> node);
	}
}
