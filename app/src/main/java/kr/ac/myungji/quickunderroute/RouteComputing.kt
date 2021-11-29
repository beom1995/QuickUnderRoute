package kr.ac.myungji.quickunderroute

import android.content.Context
import android.util.Log
import kr.ac.myungji.quickunderroute.entity.RoomEdge
import kr.ac.myungji.quickunderroute.MyApp.Companion.getApplicationContext
import kr.ac.myungji.quickunderroute.entity.RoomStation
import java.util.*

private const val INF: Int = 1000000000     // 값이 무한대(infinity)라 가정

class RouteComputing() {
    private var db: AppDatabase? = null
    private var edgeList: List<RoomEdge>? = null
    private var stationList: List<RoomStation>? = null

    private lateinit var graphTime: ArrayList<ArrayList<Node>>
    private lateinit var graphDist: ArrayList<ArrayList<Node>>
    private lateinit var graphFare: ArrayList<ArrayList<Node>>

    private var cost: IntArray = IntArray(911) { INF }
    private val stNum: Int = 111    // 역 수
    private val lastStNo: Int = 910   // 마지막 역 번호

    private var q = PriorityQueue<Node>()   // minheap을 이용하여 가장 적은 비용을 가진 경로를 계산

    init {
        val appContext: Context = getApplicationContext()
        db = DatabaseCopier.getAppDataBase(context = appContext)

        val r = Runnable {
            edgeList = db!!.roomEdgeDao().getAll()
            stationList = db!!.roomStationDao().getAll()


            // 시간, 거리, 요금을 바탕으로 그래프 구성
            graphTime = ArrayList<ArrayList<Node>>()
            graphDist = ArrayList<ArrayList<Node>>()
            graphFare = ArrayList<ArrayList<Node>>()

            for (i in 0 until lastStNo) {
                graphTime.add(ArrayList<Node>())
                graphDist.add(ArrayList<Node>())
                graphFare.add(ArrayList<Node>())
            }

            for (i in edgeList!!.indices) {
                graphTime.get(edgeList!![i].src).add(Node(edgeList!![i].dstn, edgeList!![i].timeSec))
                graphTime.get(edgeList!![i].dstn).add(Node(edgeList!![i].src, edgeList!![i].timeSec))

                graphDist.get(edgeList!![i].src).add(Node(edgeList!![i].dstn, edgeList!![i].distanceM))
                graphDist.get(edgeList!![i].dstn).add(Node(edgeList!![i].src, edgeList!![i].distanceM))

                graphFare.get(edgeList!![i].src).add(Node(edgeList!![i].dstn, edgeList!![i].fareWon))
                graphFare.get(edgeList!![i].dstn).add(Node(edgeList!![i].src, edgeList!![i].fareWon))
            }
        }
        val thread = Thread(r)
        thread.start()
    }

    fun dijkstra(src: Int, via: Int?, dstn: Int): Array<Array<Int>> {
        // 1:소요시간, 2:거리, 3:요금, 4:환승횟수
        var infoArrTime: Array<Int> = Array<Int>(4) {0}
        var infoArrDist: Array<Int> = Array<Int>(4) {0}
        var infoArrFare: Array<Int> = Array<Int>(4) {0}
        var infoArrAll: Array<Array<Int>> = arrayOf(infoArrTime, infoArrDist, infoArrFare)

        var sa: ArrayList<Int> = ArrayList<Int>()

        var cnt: Int = 0

        // 시간
        cost[src] = 0
        var curNode: Node = Node(src, 0)
        q.offer(curNode)
        while (!q.isEmpty()) {
            var preNode: Node = curNode
            curNode = q.poll()
            sa.add(curNode.index)   // 경로 추적

            for (i in edgeList!!.indices) {
                if((edgeList!![i].src == preNode.index && edgeList!![i].dstn == curNode.index)
                    || (edgeList!![i].src == curNode.index && edgeList!![i].dstn == preNode.index)) {
                        infoArrTime[1] += edgeList!![i].distanceM
                        infoArrTime[2] += edgeList!![i].fareWon
                        infoArrTime[3] += db!!.roomStationDao().getStationTransSt(curNode.index)
                        break

                }
            }

			if (curNode.index == dstn) {
			    Log.d("escape", "o")
				break
			}

            if (cost[curNode.index] < curNode.cost) {
                continue
            }

            for (i in 0 until graphTime.get(curNode.index).size) {
                var nxtNode: Node = graphTime.get(curNode.index).get(i)
                Log.d("nextnode${nxtNode.index}", curNode.cost.toString()+"//" + nxtNode.cost.toString())
                Log.d("cost[nxtNode.index]", cost[nxtNode.index].toString())
                if (cost[nxtNode.index] > curNode.cost + nxtNode.cost) {

                    cost[nxtNode.index] = curNode.cost + nxtNode.cost
                    q.offer(Node(nxtNode.index, cost[nxtNode.index]))
                }
            }
        }

        infoArrTime[0] = cost[dstn]

        Log.d("cnt", cnt.toString())

        for (c in 0 until lastStNo) {
            Log.d("cost${c}", cost[c].toString())
        }
        for (a in sa.indices) {
            Log.d("sa${a}", sa[a].toString())
        }

        // 환승역 개수 보정
        val s = Runnable {
            var start: Int = db!!.roomStationDao().getStationTransSt(src)
            var end: Int = db!!.roomStationDao().getStationTransSt(dstn)
            if(start == 1){
                infoArrTime[3] -= 1
                infoArrDist[3] -= 1
                infoArrFare[3] -= 1
            }
            if(end == 1){
                infoArrTime[3] -= 1
                infoArrDist[3] -= 1
                infoArrFare[3] -= 1
            }
        }
        val thread = Thread(s)
        thread.start()

        return infoArrAll
    }
}

data class Node(val index: Int, val cost: Int) : Comparable<Node> {
    override fun compareTo(other: Node): Int = cost-other.cost
}