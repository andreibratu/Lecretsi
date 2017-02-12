package com.glimpse.lecretsi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class UserGetToken extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String newToken = FirebaseInstanceId.getInstance().getToken();

        FirebaseDatabase.getInstance().getReference().child("push_tokens")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(newToken);
    }
}
