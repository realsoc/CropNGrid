# CropNGrid

## About the UI Architecture
### About the Navigation Component
First I struggled to decide where to put the navigation controller, I wanted a lot my ViewModel to decide when to go from a screen to another. 
I still had this impression the ViewModel should be the grand orchestrator of the UI, and so be the one to finally call navigate() on the navigation controller. 
I wanted to inject a wrapper of the navigation controller in the ViewModel (something like [this](https://medium.com/@ffvanderlaan/navigation-in-jetpack-compose-using-viewmodel-state-3b2517c24dde)). 

But after I watched a [conference on states in the UI Layer](https://www.youtube.com/watch?v=pCX9wvu-Bq0) by Manuel Vivo and I dived into the [Now In Android repository](https://github.com/android/nowinandroid), I decided to change my point of view. 
To let the ViewModel knows only what it should know in case of configuration change, and to keep the rest in the UI components, including the navigation.

So the navigation controller stays in the NavHost component. 
