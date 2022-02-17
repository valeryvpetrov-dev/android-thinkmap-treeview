package com.gyso.treeview.layout;

import android.content.Context;
import android.view.View;

import com.gyso.treeview.TreeViewContainer;
import com.gyso.treeview.adapter.TreeViewHolder;
import com.gyso.treeview.line.BaseLine;
import com.gyso.treeview.model.ITraversal;
import com.gyso.treeview.model.NodeModel;
import com.gyso.treeview.model.TreeModel;
import com.gyso.treeview.util.DensityUtils;
import com.gyso.treeview.util.ViewBox;

public class RingTreeLayoutManager extends TreeLayoutManager{
    private static final String TAG = VerticalTreeLayoutManager.class.getSimpleName();

        public RingTreeLayoutManager(Context context, int spaceParentToChild, int spacePeerToPeer, BaseLine baseline) {
            super(context, spaceParentToChild, spacePeerToPeer, baseline);
        }

        @Override
        public int getTreeLayoutType() {
            return LAYOUT_TYPE_RING;
        }

        public void performMeasureAndListen(TreeViewContainer treeViewContainer, TreeLayoutManager.MeasureListener measureListener) {
            final TreeModel<?> mTreeModel = treeViewContainer.getTreeModel();
            if (mTreeModel != null) {
                mContentViewBox.clear();
                floorMax.clear();
                deepMax.clear();
                ITraversal<NodeModel<?>> traversal = new ITraversal<NodeModel<?>>() {
                    @Override
                    public void next(NodeModel<?> next) {
                        measure(next, treeViewContainer);
                        if(measureListener!=null){
                            measureListener.onMeasureChild(next);
                        }
                    }

                    @Override
                    public void finish() {
                        //base content box
                        for (int i=0;i<floorMax.size();i++){
                            int r = floorMax.get(i);
                            mContentViewBox.right += r*2;
                            mContentViewBox.bottom += r*2;
                        }

                        //add padding
                        getPadding(treeViewContainer);
                        mContentViewBox.bottom += (paddingBox.bottom+paddingBox.top);
                        mContentViewBox.right  += (paddingBox.left+paddingBox.right);
                        fixedViewBox.setValues(mContentViewBox.top,mContentViewBox.left,mContentViewBox.right,mContentViewBox.bottom);
                        if(winHeight == 0 || winWidth==0){
                            return;
                        }
                        float scale = 1f*winWidth/winHeight;
                        float wr = 1f* mContentViewBox.getWidth()/winWidth;
                        float hr = 1f* mContentViewBox.getHeight()/winHeight;
                        if(wr>=hr){
                            float bh =  mContentViewBox.getWidth()/scale;
                            fixedViewBox.bottom = (int)bh;
                        }else{
                            float bw =  mContentViewBox.getHeight()*scale;
                            fixedViewBox.right = (int)bw;
                        }
                        mFixedDx = (fixedViewBox.getWidth()-mContentViewBox.getWidth())/2;
                        mFixedDy = (fixedViewBox.getHeight()-mContentViewBox.getHeight())/2;

                        int rootCenterX = mFixedDx+fixedViewBox.getWidth()/2;
                        int rootCenterY = mFixedDx+fixedViewBox.getHeight()/2;
                        NodeModel<?> rootNode = mTreeModel.getRootNode();
                        int leavesCount = rootNode.leafCount;
                        int anglePerLeaf  = 360 / leavesCount;


                        if(measureListener!=null){
                            measureListener.onMeasureFinished();
                        }
                    }
                };
                mTreeModel.doTraversalNodes(traversal);
            }
        }

        @Override
        public void performMeasure(TreeViewContainer treeViewContainer) {
            performMeasureAndListen(treeViewContainer,null);
        }

        /**
         * set the padding box
         * @param treeViewContainer tree view
         */
        private void getPadding(TreeViewContainer treeViewContainer) {
            if(treeViewContainer.getPaddingStart()>0){
                paddingBox.setValues(
                        treeViewContainer.getPaddingTop(),
                        treeViewContainer.getPaddingLeft(),
                        treeViewContainer.getPaddingRight(),
                        treeViewContainer.getPaddingBottom());
            }else{
                int padding = DensityUtils.dp2px(treeViewContainer.getContext(),DEFAULT_CONTENT_PADDING_DP);
                paddingBox.setValues(padding,padding,padding,padding);
            }
        }

        private void measure(NodeModel<?> node, TreeViewContainer treeViewContainer) {
            TreeViewHolder<?> currentHolder = treeViewContainer.getTreeViewHolder(node);
            View currentNodeView =  currentHolder==null?null:currentHolder.getView();
            if(currentNodeView==null){
                throw new NullPointerException(" currentNodeView can not be null");
            }
            int curH = currentNodeView.getMeasuredHeight();
            int curW = currentNodeView.getMeasuredWidth();
            int maxR = (int)Math.hypot(curH,curW);
            int preMaxR= floorMax.get(node.floor);
            if(preMaxR < maxR){
                floorMax.put(node.floor,maxR);
            }
        }

        @Override
        public void performLayout(final TreeViewContainer treeViewContainer) {
            final TreeModel<?> mTreeModel = treeViewContainer.getTreeModel();
            if (mTreeModel != null) {
                mTreeModel.doTraversalNodes(new ITraversal<NodeModel<?>>() {
                    @Override
                    public void next(NodeModel<?> next) {
                        layoutNodes(next, treeViewContainer);
                    }

                    @Override
                    public void finish() {
                        layoutAnimate(treeViewContainer);
                    }
                });
            }
        }


        @Override
        public ViewBox getTreeLayoutBox() {
            return fixedViewBox;
        }

        private void layoutNodes(NodeModel<?> currentNode, TreeViewContainer treeViewContainer){
            TreeViewHolder<?> currentHolder = treeViewContainer.getTreeViewHolder(currentNode);
            View currentNodeView =  currentHolder==null?null:currentHolder.getView();
            int deep = currentNode.deep;
            int floor = currentNode.floor;
            int leafCount = currentNode.leafCount;

            if(currentNodeView==null){
                throw new NullPointerException(" currentNodeView can not be null");
            }

            int currentWidth = currentNodeView.getMeasuredWidth();
            int currentHeight = currentNodeView.getMeasuredHeight();

            int verticalCenterFix = Math.abs(currentWidth - deepMax.get(deep))/2;

            int deltaWidth = 0;
            if(leafCount>1){
                deltaWidth = (deepStart.get(deep + leafCount) - deepStart.get(deep)-currentWidth)/2-verticalCenterFix;
                deltaWidth -= spacePeerToPeer/2;
            }

            int top = floorStart.get(floor);
            int left  = deepStart.get(deep)+verticalCenterFix+deltaWidth;
            int bottom = top+currentHeight;
            int right = left+currentWidth;

            ViewBox finalLocation = new ViewBox(top, left, bottom, right);
            if(!layoutAnimatePrepare(currentNode,currentNodeView,finalLocation,treeViewContainer)){
                currentNodeView.layout(left,top,right,bottom);
            }
        }
}