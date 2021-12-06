package kr.ac.myungji.quickunderroute

import android.content.Context
import android.util.Log
import kr.ac.myungji.quickunderroute.activity.MainActivity.Companion.TIME
import kr.ac.myungji.quickunderroute.activity.MainActivity.Companion.DIST
import kr.ac.myungji.quickunderroute.activity.MainActivity.Companion.FARE
import kr.ac.myungji.quickunderroute.entity.RoomEdge
import kr.ac.myungji.quickunderroute.entity.RoomStation
import kr.ac.myungji.quickunderroute.activity.MyApp.Companion.getApplicationContext
import java.util.*
import kotlin.collections.ArrayList

private const val INF: Int = 1000000000     // 값이 무한대(infinity)라 가정

class RouteComputing() {
    private var db: AppDatabase? = null
    private var edgeList: List<RoomEdge>? = null
    private var stationList: List<RoomStation>? = null

    private lateinit var graph: ArrayList<ArrayList<ArrayList<Node>>>   // 데이터를 그래프로 구성

    private var cost: IntArray = IntArray(911) { INF }
    private var trace: IntArray = IntArray(911) { INF }

    private val lastStNo: Int = 910   // 마지막 역 번호

    private lateinit var curNode: Node      // 경로 계산 중 현재 위치한 노드
    private var q = PriorityQueue<Node>()   // minheap을 이용하여 가장 적은 비용을 가진 경로를 계산
    var infoRoute: ArrayList<ArrayList<Int>> = ArrayList<ArrayList<Int>>()

    // 1:소요시간, 2:거리, 3:요금, 4:환승횟수
    var infoArrTime: Array<Int> = Array<Int>(4) {0}
    var infoArrDist: Array<Int> = Array<Int>(4) {0}
    var infoArrFare: Array<Int> = Array<Int>(4) {0}
    var infoArrAll: Array<Array<Int>> = arrayOf(infoArrTime, infoArrDist, infoArrFare)  // 경로 계산 결과

    init {
        val appContext: Context = getApplicationContext()
        db = DatabaseCopier.getAppDataBase(context = appContext)

        val r = Runnable {
            // 역과 역간 비용 정보 가져오기
            edgeList = db!!.roomEdgeDao().getAll()
            stationList = db!!.roomStationDao().getAll()

            // 시간, 거리, 요금을 바탕으로 그래프 구성
            graph = ArrayList<ArrayList<ArrayList<Node>>>()

            for (k in 0 until 3) {
                graph.add(ArrayList<ArrayList<Node>>())
            }

            for (j in 0 until lastStNo) {
                graph[TIME].add(ArrayList<Node>())
                graph[DIST].add(ArrayList<Node>())
                graph[FARE].add(ArrayList<Node>())
            }

            // 인접리스트
            for (i in edgeList!!.indices) {
                graph[TIME][edgeList!![i].src].add(Node(edgeList!![i].dstn, edgeList!![i].timeSec))
                graph[TIME][edgeList!![i].dstn].add(Node(edgeList!![i].src, edgeList!![i].timeSec))

                graph[DIST][edgeList!![i].src].add(Node(edgeList!![i].dstn, edgeList!![i].distanceM))
                graph[DIST][edgeList!![i].dstn].add(Node(edgeList!![i].src, edgeList!![i].distanceM))

                graph[FARE][edgeList!![i].src].add(Node(edgeList!![i].dstn, edgeList!![i].fareWon))
                graph[FARE][edgeList!![i].dstn].add(Node(edgeList!![i].src, edgeList!![i].fareWon))
            }
        }
        val thread = Thread(r)
        thread.start()

        for (o in 0 until 3) {
            infoRoute.add(ArrayList<Int>())
        }

    }

    // 최단시간, 최단거리, 최저요금 다익스트라 계산
    fun dijkstra(src: Int, via: Int?, dstn: Int): Array<Array<Int>> {

        // 경로 추적용
        var preTrace: Int
        var curTrace: Int = 0
        var passby = 0

        // 최단시간 계산
        cost = IntArray(911) { INF }
        trace = IntArray(911) { INF }
        cost[src] = 0
        curNode = Node(src, 0)

        if(via != null){
            dijkstraCommon(src, via, TIME)
            dijkstraCommon(via, dstn, TIME)
        } else {
            dijkstraCommon(src, dstn, TIME)
        }
        infoArrTime[TIME] = cost[dstn]
        traceRoute(src, dstn, TIME)
        getTransCnt(src, dstn, TIME)

        for(i in infoRoute[TIME].indices) {
            passby++
            preTrace = curTrace
            curTrace = infoRoute[TIME][i]
            Log.d("trace$passby", curTrace.toString())

            for (j in edgeList!!.indices) {
                if (curTrace == src) {
                    break
                }
                if ((edgeList!![j].src == preTrace && edgeList!![j].dstn == curTrace)
                    || (edgeList!![j].dstn == preTrace && edgeList!![j].src == curTrace)) {
                    infoArrTime[1] += edgeList!![j].distanceM
                    infoArrTime[2] += edgeList!![j].fareWon
                }
            }
        }
        q.clear()


        // 최단거리 계산
        cost = IntArray(911){ INF }
        trace = IntArray(911) { INF }
        cost[src] = 0
        curNode = Node(src, 0)
        curTrace = 0
        passby = 0

        if(via != null){
            dijkstraCommon(src, via, DIST)
            dijkstraCommon(via, dstn, DIST)
        } else {
            dijkstraCommon(src, dstn, DIST)
        }
        infoArrDist[DIST] = cost[dstn]
        traceRoute(src, dstn, DIST)
        getTransCnt(src, dstn, DIST)

        for(i in infoRoute[DIST].indices) {
            passby++
            preTrace = curTrace
            curTrace = infoRoute[DIST][i]
            Log.d("trace$passby", curTrace.toString())

            for (j in edgeList!!.indices) {
                if (curTrace == src) {
                    break
                }
                if ((edgeList!![j].src == preTrace && edgeList!![j].dstn == curTrace)
                    || (edgeList!![j].dstn == preTrace && edgeList!![j].src == curTrace)) {
                    infoArrDist[0] += edgeList!![j].timeSec
                    infoArrDist[2] += edgeList!![j].fareWon
                }
            }
        }
        q.clear()


        // 최저요금 계산
        cost = IntArray(911){ INF }
        trace = IntArray(911) { INF }
        cost[src] = 0
        curNode = Node(src, 0)
        curTrace = 0
        passby = 0

        if(via != null){
            dijkstraCommon(src, via, FARE)
            dijkstraCommon(via, dstn, FARE)
        } else {
            dijkstraCommon(src, dstn, FARE)
        }

        infoArrFare[FARE] = cost[dstn]
        traceRoute(src, dstn, FARE)
        getTransCnt(src, dstn, FARE)

        for(i in infoRoute[FARE].indices) {
            passby++
            preTrace = curTrace
            curTrace = infoRoute[FARE][i]
            Log.d("trace$passby", curTrace.toString())

            for (j in edgeList!!.indices) {
                if (curTrace == src) {
                    break
                }
                if ((edgeList!![j].src == preTrace && edgeList!![j].dstn == curTrace)
                    || (edgeList!![j].dstn == preTrace && edgeList!![j].src == curTrace)) {
                    infoArrFare[0] += edgeList!![j].timeSec
                    infoArrFare[1] += edgeList!![j].distanceM
                }
            }
        }
        q.clear()

        return infoArrAll
    }

    // 다익스트라 계산에서 공통적인 부분(type-0:최단시간, 1:최단거리, 2:최저요금
    private fun dijkstraCommon(src: Int, dstn: Int, type: Int) {
        q.offer(curNode)

        while (!q.isEmpty()) {
            curNode = q.poll()
            
            // 효율성을 위해 모든 경로를 계산하지 않고 목적지까지의 경로만 계산
			if (curNode.index == dstn) {
				break
			}

            // 이미 최소값이 구해진 역에 대해선 계산 안 함
            if (cost[curNode.index] < curNode.cost) {
                continue
            }

            // 현재 역과 연결된 역의 비용 탐색
            for (i in 0 until graph[type][curNode.index].size) {
                var nxtNode: Node = graph[type][curNode.index][i]

                // 새로 계산한 비용이 더 작을 때만 갱신
                if (cost[nxtNode.index] > curNode.cost + nxtNode.cost) {
                    cost[nxtNode.index] = curNode.cost + nxtNode.cost
                    q.offer(Node(nxtNode.index, cost[nxtNode.index]))
                    trace[nxtNode.index] = curNode.index
                }
            }
        }
    }

    // 최소비용 계산한 경로 추적
    private fun traceRoute(src: Int, dstn: Int, type: Int){
        var cur = dstn

        while(cur != src) {
            infoRoute[type].add(cur)
            cur = trace[cur]
        }
        infoRoute[type].add(cur)
    }

    // 최소비용 계산한 경로 제공
    fun getRoute() : ArrayList<ArrayList<Int>> {
        return infoRoute
    }

    // 환승 횟수 계산
    private fun getTransCnt(src: Int, dstn: Int, type: Int) {
        val s = Runnable {
            for(e in infoRoute.indices) {
                if ((src != infoRoute[type][e] || dstn != infoRoute[type][e])
                    && db!!.roomStationDao().getStationTransSt(infoRoute[type][e]) == 1) {
                    infoArrAll[type][3] += 1
                }
            }
        }
        val thread = Thread(s)
        thread.start()
    }
}

data class Node(val index: Int, val cost: Int) : Comparable<Node> {
    override fun compareTo(other: Node): Int = cost-other.cost
}