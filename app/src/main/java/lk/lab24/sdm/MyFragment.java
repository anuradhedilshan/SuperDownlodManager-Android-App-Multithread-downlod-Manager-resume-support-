package lk.lab24.sdm;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MyFragment extends Fragment {


	private static final String TAG = "fuck";
	RecyclerView recyclerView;
	private int pos = 0;

	MyFragment(int pos) {
		this.pos = pos;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View inflate = inflater.inflate(R.layout.myfragment, null, false);
		recyclerView = inflate.findViewById(R.id.recycler_view);
		RecycleViewAdapter recycleViewAdapter = new RecycleViewAdapter(getContext(),pos);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setAdapter(recycleViewAdapter);
		Log.d(TAG, "onCreateView: framgent ");
		return inflate;

	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
