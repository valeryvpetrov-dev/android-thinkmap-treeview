package com.gyso.gysotreeviewapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gyso.gysotreeviewapplication.databinding.NodeBaseLayoutBinding;
import com.gyso.gysotreeviewapplication.model.AbstractScene;
import com.gyso.treeview.adapter.DrawInfo;
import com.gyso.treeview.adapter.TreeViewAdapter;
import com.gyso.treeview.adapter.TreeViewHolder;
import com.gyso.treeview.line.BaseLine;
import com.gyso.treeview.model.NodeModel;

public class SceneTreeViewAdapter extends TreeViewAdapter<AbstractScene> {
	private OnItemClickListener listener;

	public void setOnItemListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	@Override
	public TreeViewHolder<AbstractScene> onCreateViewHolder(@NonNull ViewGroup viewGroup, NodeModel<AbstractScene> node) {
		NodeBaseLayoutBinding nodeBinding = NodeBaseLayoutBinding.inflate(
				LayoutInflater.from(viewGroup.getContext()),
				viewGroup,
				false
		);
		return new TreeViewHolder<>(nodeBinding.getRoot(), node);
	}

	@Override
	public void onBindViewHolder(@NonNull TreeViewHolder<AbstractScene> holder) {
		View itemView = holder.getView();
		NodeModel<AbstractScene> node = holder.getNode();
		TextView idTextView = itemView.findViewById(R.id.id);
		final AbstractScene scene = node.value;
		idTextView.setText(scene.getId());
		itemView.setOnClickListener(v -> {
			if (listener != null) listener.onItemClick(v, node);
		});
	}

	@Override
	public BaseLine onDrawLine(DrawInfo drawInfo) {
		return null;
	}

	public interface OnItemClickListener {
		void onItemClick(View item, NodeModel<AbstractScene> node);
	}
}
