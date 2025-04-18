import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.UserProfileData

class UserProfileViewModel : ViewModel() {
    private val _userProfileData = mutableStateOf<UserProfileData?>(null)
    val userProfileData: State<UserProfileData?> = _userProfileData

    fun fetchUserProfile(userId: Int) {
        viewModelScope.launch {
            try {
                val data = RetrofitClient.userProfileService.getUserProfileById(userId)
                _userProfileData.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
