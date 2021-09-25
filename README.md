# ChartView

Implementation of Android customizable SurfaceView for displaying real-time data.

The library also supports data approximation.  
#

<img src=https://user-images.githubusercontent.com/3678050/134302405-ce836faf-1dad-4051-8a6d-0e4f7606189b.gif width="270" height="585">

## **Usage**

Add it in your root build.gradle at the end of repositories:

    repositories {
	    ...
        maven { url "https://jitpack.io" }
    }	
Add the dependency

    dependencies {
        ...
        implementation 'com.github.YablokovDmitry:ChartView:1.4'
    }
### xml

     <com.ydn.chartview.ChartView
         android:id="@+id/chart"
         android:layout_width="match_parent"
         android:layout_height="match_parent" />
#### onCreate()
       // Initialization
       chartView = findViewById(R.id.chart)
       with(chartView) {
            title = "Sample2 (1500 ms) Float"
            chartColorString = "#6CEB2B"
            textColorString = "#000000"
            titleColorString = "#BABABA"
            range = 15 * 100L
            curveWidth = 3
            
            // Show all data with approximation
            showAllData = false
        }
#### onResume()
       override fun onResume() {
           super.onResume()

           // Simulate real data with timer
           timer1 = Timer() 
           timer1.scheduleAtFixedRate(
              timerTask {
                  val x = SystemClock.elapsedRealtime()
                  val y = Random().nextFloat() * 10f - 5f
                  chartView.add(x, y)
              }, 0, 10)
        }      
#### onPause()
       override fun onPause() {
           super.onPause()
           
           chartView.pause()
       }
       

### **Developed By**
  - Dmitry Yablokov - [dnyablokov@gmail.com](mailto:dnyablokov@gmail.com)


  ### **License**
```      

Copyright 2021 Dmitry Yablokov.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```      

