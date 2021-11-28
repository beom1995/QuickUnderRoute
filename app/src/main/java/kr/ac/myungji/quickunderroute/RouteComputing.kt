package kr.ac.myungji.quickunderroute

import android.content.Context
import android.util.Log
import kr.ac.myungji.quickunderroute.entity.RoomEdge
import kr.ac.myungji.quickunderroute.MyApp.Companion.getApplicationContext
import java.util.*

private const val INF: Int = 1000000000     // 값이 무한대(infinity)라 가정

class RouteComputing() {
    private var db: AppDatabase? = null
    private var edgeList: List<RoomEdge>? = null

    private lateinit var cost: IntArray
    private lateinit var vis: BooleanArray

    // minheap을 이용하여 가장 적은 비용을 가진 경로를 계산
    private val queueTime = PriorityQueue<Node>()
    private val queueDist = PriorityQueue<Node>()
    private val queueFare = PriorityQueue<Node>()

    init {
        val appContext: Context = getApplicationContext()
        db = DatabaseCopier.getAppDataBase(context = appContext)

        val r = Runnable {
            edgeList = db!!.roomEdgeDao().getAll()
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

        var curSrc = src
        vis = BooleanArray(111)

        queueTime.add(Node(src, 0))
        queueDist.add(Node(src, 0))
        queueFare.add(Node(src, 0))

        // 최단 시간 계산
        cost = IntArray(910) { INF }
        cost[curSrc] = 0 // 시작 거리는 0

        while (queueTime.isNotEmpty()) {
            var prevSrc = curSrc
            curSrc = queueTime.peek().index  // 현재 노드 인덱스
            if (edgeList != null) {
                for (i in edgeList!!.indices) {
                    //최단시간
                    if(edgeList!![i].src == prevSrc && edgeList!![i].dstn == curSrc) {
                        infoArrTime[1] += edgeList!![i].distanceM
                        infoArrTime[2] += edgeList!![i].fareWon
                        infoArrTime[3] += db!!.roomStationDao().getStationTransSt(curSrc)
                    }
                }
            }
            val curCost = queueTime.peek().cost  // 현재 노드까지의 비용
            queueTime.poll()

            if (cost[curSrc] < curCost) continue // 탐색 시간을 줄이기 위해
            // 현재 거리가 현재 노드까지의 거리보다 작으면 탐색 중단

            if (edgeList != null) {
                for (i in edgeList!!.indices) { // 연결된 노드들 탐색
                    //최단시간
                    if(edgeList!![i].src == curSrc) {
                        val nextIndex = edgeList!![i].dstn
                        val nextCost = curCost + edgeList!![i].timeSec

                        if (nextCost < cost[nextIndex]) {
                            cost[nextIndex] = nextCost
                            queueTime.add(Node(nextIndex, nextCost))
                        }
                    }
                }
            }
        }
        infoArrTime[0] = cost[dstn]


        // 최단 거리 계산
        curSrc = src
        cost = IntArray(910) { INF }
        cost[curSrc] = 0 // 시작 거리는 0
        while (queueDist.isNotEmpty()) {
            var prevSrc = curSrc
            curSrc = queueDist.peek().index  // 현재 노드 인덱스
            if (edgeList != null) {
                for (i in edgeList!!.indices) {
                    //최단시간
                    if(edgeList!![i].src == prevSrc && edgeList!![i].dstn == curSrc) {
                        infoArrDist[0] += edgeList!![i].timeSec
                        infoArrDist[2] += edgeList!![i].fareWon
                        infoArrDist[3] += db!!.roomStationDao().getStationTransSt(curSrc)
                    }
                }
            }
            val curCost = queueDist.peek().cost  // 현재 노드까지의 비용
            queueDist.poll()

            if (cost[curSrc] < curCost) continue // 탐색 시간을 줄이기 위해
            // 현재 거리가 현재 노드까지의 거리보다 작으면 탐색 중단

            if (edgeList != null) {
                for (i in edgeList!!.indices) { // 연결된 노드들 탐색
                    //최단시간
                    if(edgeList!![i].src == curSrc) {
                        val nextIndex = edgeList!![i].dstn
                        val nextCost = curCost + edgeList!![i].distanceM

                        if (nextCost < cost[nextIndex]) {
                            cost[nextIndex] = nextCost
                            queueDist.add(Node(nextIndex, nextCost))
                        }
                    }
                }
            }
        }
        infoArrDist[1] = cost[dstn]

        
        // 최저 요금 계산
        curSrc = src
        cost = IntArray(910) { INF }
        cost[curSrc] = 0 // 시작 거리는 0
        while (queueFare.isNotEmpty()) {
            var prevSrc = curSrc
            curSrc = queueFare.peek().index  // 현재 노드 인덱스
            if (edgeList != null) {
                for (i in edgeList!!.indices) {
                    //최단시간
                    if(edgeList!![i].src == prevSrc && edgeList!![i].dstn == curSrc) {
                        infoArrFare[0] += edgeList!![i].timeSec
                        infoArrFare[1] += edgeList!![i].distanceM
                        infoArrFare[3] += db!!.roomStationDao().getStationTransSt(curSrc)
                    }
                }
            }
            val curCost = queueFare.peek().cost  // 현재 노드까지의 비용
            queueFare.poll()    // 지나간 길 제외

            if (cost[curSrc] < curCost) continue // 탐색 시간을 줄이기 위해
            // 현재 거리가 현재 노드까지의 거리보다 작으면 탐색 중단

            if (edgeList != null) {
                for (i in edgeList!!.indices) { // 연결된 노드들 탐색
                    //최단시간
                    if (edgeList!![i].src == curSrc) {
                        val nextIndex = edgeList!![i].dstn
                        val nextCost = curCost + edgeList!![i].fareWon

                        if (nextCost < cost[nextIndex]) {
                            cost[nextIndex] = nextCost
                            queueFare.add(Node(nextIndex, nextCost))
                        }
                    }
                }
            }
        }
        infoArrFare[2] = cost[dstn]

        // 환승역 개수 처리
        var no: Int? = null
        val s = Runnable {
            no = db!!.roomStationDao().getStationTransSt(dstn)
            if(no == 1){
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