# ChartView

Implementation of Android customizable SurfaceView for displaying real-time data.

The library also supports data approximation.  
#

<img src=https://user-images.githubusercontent.com/3678050/134299739-52951d99-1842-4185-aa46-cf998a8e301b.gif width="270" height="585">

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

MIT License

Copyright (c) 2021 Dmitry Yablokov

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

```      

