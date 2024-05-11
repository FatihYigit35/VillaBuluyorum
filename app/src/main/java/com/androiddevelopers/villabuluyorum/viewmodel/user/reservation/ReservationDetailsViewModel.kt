package com.androiddevelopers.villabuluyorum.viewmodel.user.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.ReservationModel
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toReservation
import com.androiddevelopers.villabuluyorum.util.toUserModel
import com.androiddevelopers.villabuluyorum.util.toVilla
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationDetailsViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
) : ViewModel() {

    private var _reservationMessage = MutableLiveData<Resource<Boolean>>()
    val reservationMessage: LiveData<Resource<Boolean>>
        get() = _reservationMessage

    private var _reservation = MutableLiveData<ReservationModel>()
    val reservation : LiveData<ReservationModel>
        get() =  _reservation

    private var _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData

    private var _liveDataFirebaseVilla = MutableLiveData<Villa>()
    val liveDataFirebaseVilla: LiveData<Villa>
        get() = _liveDataFirebaseVilla

    fun getReservationById(reservationId : String) = viewModelScope.launch {
        _reservationMessage.value = Resource.loading(true)
        firebaseRepo.getReservationById(reservationId)
            .addOnSuccessListener {
                it.toReservation()?.let { reservation ->
                    _reservation.value = reservation
                }
                _reservationMessage.value = Resource.success( true)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _reservationMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
    fun getUserDataById(userId : String) = viewModelScope.launch {
        firebaseRepo.getUserDataByDocumentId(userId)
            .addOnSuccessListener { document ->
                document.toUserModel()?.let { user ->
                    _userData.value = user
                }
            }
    }


    fun getVillaById(villaId: String) {
        firebaseRepo.getVillaByIdFromFirestore(villaId)
            .addOnSuccessListener { documentSnapshot ->
                _liveDataFirebaseVilla.value = documentSnapshot.toVilla()
            }
    }
}