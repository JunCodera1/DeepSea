import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.deepsea.data.api.UserProfileService
import com.example.deepsea.ui.viewmodel.leaderboard.LeaderboardViewModel

class LeaderboardViewModelFactory(
    private val userProfileService: UserProfileService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaderboardViewModel::class.java)) {
            return LeaderboardViewModel(userProfileService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
