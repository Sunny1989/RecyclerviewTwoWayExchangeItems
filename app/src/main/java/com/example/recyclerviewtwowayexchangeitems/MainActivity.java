package com.example.recyclerviewtwowayexchangeitems;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;

import com.example.recyclerviewtwowayexchangeitems.databinding.MainActivityBinding;

public class MainActivity extends AppCompatActivity implements View.OnDragListener {

    private MainActivityBinding mainActivityBinding;
    public ObservableArrayList<ExcercisePojo> exerciseList;
    public ObservableArrayList<ExcercisePojo> exerciseSelectedList = new ObservableArrayList<>();
    public ExcercisePojo exerciseToMove;
    private int newContactPosition = -1;

    private int currentPosition = -1;
    private boolean isExerciseAdded = false;
    public static boolean isFromExercise = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        loadExerciseData();
        mainActivityBinding.setMainActivity(this);
        mainActivityBinding.rcvSelectedExercise.setOnDragListener(this);


        mainActivityBinding.rcvChooseExercise.setOnDragListener(new MyDragInsideRcvListener());
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.scale_3dp);
        mainActivityBinding.rcvChooseExercise.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }


    public void loadExerciseData() {
        exerciseList = new ObservableArrayList<>();
        for (int i = 1; i <= 10; i++) {
            exerciseList.add(new ExcercisePojo(i, "exercise " + i));
        }
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        View selectedView = (View) dragEvent.getLocalState();
        RecyclerView rcvSelected = (RecyclerView) view;

        try {
            int currentPosition = mainActivityBinding.rcvChooseExercise.getChildAdapterPosition(selectedView);

            // Ensure the position is valid.
            if (currentPosition != -1) {
                exerciseToMove = exerciseList.get(currentPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (dragEvent.getAction()) {
            case DragEvent.ACTION_DRAG_LOCATION:
                View onTopOf = rcvSelected.findChildViewUnder(dragEvent.getX(), dragEvent.getY());
                newContactPosition = rcvSelected.getChildAdapterPosition(onTopOf);
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DROP:
                //when Item is dropped off to recyclerview.
                if (isFromExercise) {
                    exerciseSelectedList.add(exerciseToMove);
                    exerciseList.remove(exerciseToMove);
                    mainActivityBinding.rcvChooseExercise.getAdapter().notifyItemRemoved(currentPosition);
                    mainActivityBinding.executePendingBindings();
                }
                //This is to hide/add the container!
                /*ViewGroup owner = (ViewGroup) (view.getParent());
                if (owner != null) {
                    //owner.removeView(selectedView);
                    //owner.addView(selectedView);

                    try {
                        LinearLayout rlContainer = (LinearLayout) view;
                        rlContainer.addView(selectedView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //selectedView.setVisibility(View.VISIBLE);
                }*/

                break;

            case DragEvent.ACTION_DRAG_ENDED:
                // Reset the visibility for the Contact item's view.
                // This is done to reset the state in instances where the drag action didn't do anything.
                selectedView.setVisibility(View.VISIBLE);
                // Boundary condition, scroll to top is moving list item to position 0.
                if (newContactPosition != -1) {
                    rcvSelected.scrollToPosition(newContactPosition);
                    newContactPosition = -1;
                } else {
                    rcvSelected.scrollToPosition(0);
                }
            default:
                break;
        }
        return true;
    }

    /**
     * This listener class is for Vertical Recyclerview.
     */
    class MyDragInsideRcvListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            RecyclerView rcv = (RecyclerView) v;

            View selectedView = (View) event.getLocalState();
            try {
                int currentPosition = rcv.getChildAdapterPosition(selectedView);
                // Ensure the position is valid.
                if (currentPosition != -1) {
                    exerciseToMove = exerciseSelectedList.get(currentPosition);
                    //exerciseSelectedList.get(currentPosition);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_LOCATION:
                    View onTopOf = rcv.findChildViewUnder(event.getX(), event.getY());
                    newContactPosition = rcv.getChildAdapterPosition(onTopOf);

                    //Flag for our own understanding!
                    //isFromExercise = true;

                    //This is for internal dragging (inside recyclerview only).  VVIP!
                    // Ensure the new position is valid.

                    //If you wanted to swap the items in recyclerview only.
                    //It requires bit changes.
                   /* if (newContactPosition != -1) {
                        LinearLayoutManager layoutManager = (LinearLayoutManager) rcv.getLayoutManager();
                        int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

                        // Scroll up or down one if we are over the first or last visible list item.
                        if (newContactPosition >= lastVisiblePosition)
                            layoutManager.scrollToPositionWithOffset(firstVisiblePosition + 1, 0);
                        else if (newContactPosition <= firstVisiblePosition)
                            layoutManager.scrollToPositionWithOffset(firstVisiblePosition - 1, 0);

                        // Update the location of the Contact
                        exerciseList.remove(exerciseToMove);
                        exerciseList.add(newContactPosition, exerciseToMove);
                        rcv.getAdapter().notifyDataSetChanged();
                    }*/
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    // Reset the visibility for the Contact item's view.
                    // This is done to reset the state in instances where the drag action didn't do anything.
                    selectedView.setVisibility(View.VISIBLE);
                    // Boundary condition, scroll to top is moving list item to position 0.
                    if (newContactPosition != -1) {
                        rcv.scrollToPosition(newContactPosition);
                        newContactPosition = -1;
                    } else {
                        rcv.scrollToPosition(0);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    if (!isFromExercise) {
                        //THIS IS FOR WHEN WE TAKE ITEM OF OTHER LIST AND DROP IN THIS LIST.
                        exerciseList.add(exerciseToMove);
                        exerciseSelectedList.remove(exerciseToMove);
                        mainActivityBinding.rcvChooseExercise.getAdapter().notifyItemInserted(currentPosition);
                        mainActivityBinding.executePendingBindings();
                    }
                    break;

                default:
                    break;
            }
            return true;
        }
    }
}

