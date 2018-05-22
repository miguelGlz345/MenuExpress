package com.menuexpress.equipo6.menuexpress.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.menuexpress.equipo6.menuexpress.Common.Common;
import com.menuexpress.equipo6.menuexpress.Model.Token;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        if (Common.currentUser != null)
            updateTokenFirebase(tokenRefreshed);
    }

    private void updateTokenFirebase(String tokenRefreshed) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("tokens");
        Token token = new Token(tokenRefreshed, false); //false porque este token es enviado del Cliente
        tokens.child(Common.currentUser.getUid()).setValue(token);
    }
}
