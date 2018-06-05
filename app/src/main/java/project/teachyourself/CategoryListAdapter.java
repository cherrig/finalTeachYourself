package project.teachyourself;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.List;

import project.teachyourself.model.Category;

/**
 * Adapters provide a binding from an app-specific data set to views
 * that are displayed within a RecyclerView.
 */
public class CategoryListAdapter
        extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder> {

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemTitle;
        private final FlexboxLayout itemContainer;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemContainer = itemView.findViewById(R.id.itemContainer);
        }
    }

    private LayoutInflater inflater;
    private List<Category> categories;

    /**
     * Constructor
     * @param context context
     */
    public CategoryListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    /**
     * Called when RecyclerView needs a new {@link android.support.v7.widget.RecyclerView.ViewHolder}
     * of the given type to represent an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int, List)}.
     * Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int)
     */
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(itemView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link android.support.v7.widget.RecyclerView.ViewHolder#itemView}
     * to reflect the item at the given position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use
     * {@link android.support.v7.widget.RecyclerView.ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder, int, List)}
     * instead if Adapter can handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {
        if (categories != null) {
            Category category = categories.get(position);
            holder.itemTitle.setText(category.getTitle());
            holder.itemTitle.setCompoundDrawablesWithIntrinsicBounds(category.getIcon(), 0, 0, 0);

            // Items takes all the place in the screen
            ViewGroup.LayoutParams lp = holder.itemContainer.getLayoutParams();
            if (lp instanceof FlexboxLayoutManager.LayoutParams) {
                FlexboxLayoutManager.LayoutParams flexboxLp =
                        (FlexboxLayoutManager.LayoutParams) holder.itemContainer.getLayoutParams();
                flexboxLp.setFlexGrow(1.0f);
            }

            // Set the Gradient background to fake a shadow effect
            holder.itemContainer.setBackgroundResource(category.getShadow());
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (categories != null)
            return categories.size();
        return 0;
    }

    /**
     * Set the List of categories to display in the RecyclerView
     * @param categories List of categories
     */
    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    /**
     * Get the Category at a given position in the RecyclerView
     * @param position position in the RecyclerView
     * @return the Category at the position
     */
    public Category getCategoryAtPosition(int position) {
        if (categories != null && categories.size() > position)
            return categories.get(position);
        else
            return null;
    }

}
