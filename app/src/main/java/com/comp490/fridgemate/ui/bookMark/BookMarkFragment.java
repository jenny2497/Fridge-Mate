package com.comp490.fridgemate.ui.bookMark;



import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.comp490.fridgemate.R;
import com.comp490.fridgemate.databinding.FragmentBookmarkBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BookMarkFragment extends Fragment {
    ProgressDialog dialog;
    private FragmentBookmarkBinding binding;
    View root;
    ListView folders;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentFirebaseUser;
    DocumentReference foldersDocRef;
    String user;
    ArrayList<String> folderNames = new ArrayList<>();
    ArrayAdapter<String> foldersAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        dialog = new ProgressDialog((getActivity()));
        dialog.setTitle("Loading...");
        dialog.show();
        folders = root.findViewById(R.id.listView_folders);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        user = currentFirebaseUser.getUid();
        foldersDocRef = db.collection("users/" + user + "/categories").document("folders");

        fetchFromDatabase();
        return root;
    }

    private void fetchFromDatabase() {

        foldersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        folderNames = (ArrayList<String>) document.getData().get("folders");
                        Log.d("TAG", "DocumentSnapshot data: " + folderNames);
                        foldersAdapter = new ArrayAdapter<String>(root.getContext(), R.layout.folder_item, R.id.folder_name, folderNames) {
                            @Override
                            public View getView(final int position, View convertView, ViewGroup parent) {
                                View inflatedView = super.getView(position, convertView, parent);
                                return inflatedView;
                            };
                        };

                        folders.setAdapter(foldersAdapter);
                    } else {
                        Log.d("TAG", "No such document");
                        Map<String, Object> foldersData = new HashMap<>();
                        foldersData.put("folders", Arrays.asList("Favorites", "My Recipes"));

                        foldersAdapter = new ArrayAdapter<String>(root.getContext(), R.layout.folder_item, R.id.folder_name, folderNames);
                        folders.setAdapter(foldersAdapter);
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                    foldersAdapter = new ArrayAdapter<String>(root.getContext(), R.layout.folder_item, R.id.folder_name, folderNames);
                    folders.setAdapter(foldersAdapter);
                }
            }
        });


    }
}
