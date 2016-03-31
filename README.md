# PopularMovies
an app to allow users to discover the most popular movies playing
* present the user with a grid arrangement of movie posters upon launch.
* allow user to change sort order via a setting: The sort order can be by most popular or by highest-rated
 * original title
 * movie poster image thumbnail
 * A plot synopsis (called overview in the api)
 * user rating (called vote_average in the api)
 * release date
* allow users to view and play trailers ( either in the youtube app or a web browser).
* allow users to read reviews of a selected movie.
* allow users to mark a movie as a favorite in the details view by tapping a button(star). This is for a local movies collection that you will maintain and does not require an API request*.

#the Moviedb.org API
Please obtain an API key through https://www.themoviedb.org/

Then replace
<string name="movie_api_key">YOUR_API_KEY</string>
under /res/values/api.xml with your API key.
