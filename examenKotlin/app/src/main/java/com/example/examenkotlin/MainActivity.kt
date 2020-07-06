package com.example.examenkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.examenkotlin.network.Repository
import com.example.examenkotlin.network.UserResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callService()

        text_feed.setOnClickListener{
            view ->

            val intent = Intent(this, Feed::class.java)
            startActivity(intent)


        }
    }

    private fun callService() {
        val service = Repository.RetrofitRepository.getService()

        GlobalScope.launch(Dispatchers.IO) {
            val response =  service.getProfile()

            withContext(Dispatchers.Main) {
                try {
                    if(response.isSuccessful) {

                        val user : UserResponse?  = response.body()
                        if( user != null) updateDates(user)
                    }else{
                        Toast.makeText(this@MainActivity, "Error ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }catch (e : HttpException) {
                    Toast.makeText(this@MainActivity, "Error ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun updateDates(user: UserResponse) {
        if(user.image.isNotEmpty()){
            Picasso.get().load(user.image).into(imgen_person)
        }

        text_like.text = user.social.likes.toString()
        text_post.text = user.social.posts.toString()
        text_share.text = user.social.shares.toString()
        text_friends.text = user.social.friends.toString()

        txt_nombre.text = String.format("%s %s", user.name, user.lastname)
        text_email.text = user.email
        text_years.text = user.age
        text_location.text = user.location
        text_work.text = user.occupation



    }

}