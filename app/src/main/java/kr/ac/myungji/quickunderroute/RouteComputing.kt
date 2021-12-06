package kr.ac.myungji.quickunderroute

import android.content.Context
import android.util.Log
import kr.ac.myungji.quickunderroute.entity.RoomEdge
import kr.ac.myungji.quickunderroute.MyApp.Companion.getApplicationContext
import kr.ac.myungji.quickunderroute.entity.RoomStation
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
    private var stack: Stack<Int> = Stack<Int>()    // 경로 추적을 위해 사용
    
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
                graph[0].add(ArrayList<Node>())
                graph[1].add(ArrayList<Node>())
                graph[2].add(ArrayList<Node>())
            }

            // 인접리스트
            for (i in edgeList!!.indices) {
                graph[0][edgeList!![i].src].add(Node(edgeList!![i].dstn, edgeList!![i].timeSec))
                graph[0][edgeList!![i].dstn].add(Node(edgeList!![i].src, edgeList!![i].timeSec))

                graph[1][edgeList!![i].src].add(Node(edgeList!![i].dstn, edgeList!![i].distanceM))
                graph[1][edgeList!![i].dstn].add(Node(edgeList!![i].src, edgeList!![i].distanceM))

                graph[2][edgeList!![i].src].add(Node(edgeList!![i].dstn, edgeList!![i].fareWon))
                graph[2][edgeList!![i].dstn].add(Node(edgeList!![i].src, edgeList!![i].fareWon))
            }
        }
        val thread = Thread(r)
        thread.start()
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
            dijkstraCommon(src, via, 0)
            dijkstraCommon(via, dstn, 0)
        } else {
            dijkstraCommon(src, dstn, 0)
        }
        infoArrTime[0] = cost[dstn]
        traceRoute(src, dstn)

        while(!stack.isEmpty()){
            passby++
            preTrace = curTrace
            curTrace = stack.pop()
            Log.d("trace$passby", curTrace.toString())

            for (j in edgeList!!.indices) {
                if (curTrace == src) {
                    break
                }
                if ((edgeList!![j].src == preTrace && edgeList!![j].dstn == curTrace)
                    || (edgeList!![j].dstn == preTrace && edgeList!![j].src == curTrace)) {
                    infoArrTime[1] += edgeList!![j].distanceM
                    infoArrTime[2] += edgeList!![j].fareWon
                    infoArrTime[3] += db!!.roomStationDao().getStationTransSt(curTrace)
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
            dijkstraCommon(src, via, 1)
            dijkstraCommon(via, dstn, 1)
        } else {
            dijkstraCommon(src, dstn, 1)
        }
        infoArrDist[1] = cost[dstn]
        traceRoute(src, dstn)

        while(!stack.isEmpty()){
            passby++
            preTrace = curTrace
            curTrace = stack.pop()
            Log.d("trace$passby", curTrace.toString())

            for (j in edgeList!!.indices) {
                if (curTrace == src) {
                    break
                }
                if ((edgeList!![j].src == preTrace && edgeList!![j].dstn == curTrace)
                    || (edgeList!![j].dstn == preTrace && edgeList!![j].src == curTrace)) {
                    infoArrDist[0] += edgeList!![j].timeSec
                    infoArrDist[2] += edgeList!![j].fareWon
                    infoArrDist[3] += db!!.roomStationDao().getStationTransSt(curTrace)
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
            dijkstraCommon(src, via, 2)
            dijkstraCommon(via, dstn, 2)
        } else {
            dijkstraCommon(src, dstn, 2)
        }

        infoArrFare[2] = cost[dstn]
        traceRoute(src, dstn)

        while(!stack.isEmpty()){
            passby++
            preTrace = curTrace
            curTrace = stack.pop()
            Log.d("trace$passby", curTrace.toString())

            for (j in edgeList!!.indices) {
                if (curTrace == src) {
                    break
                }
                if ((edgeList!![j].src == preTrace && edgeList!![j].dstn == curTrace)
                    || (edgeList!![j].dstn == preTrace && edgeList!![j].src == curTrace)) {
                    infoArrFare[0] += edgeList!![j].timeSec
                    infoArrFare[1] += edgeList!![j].distanceM
                    infoArrFare[3] += db!!.roomStationDao().getStationTransSt(curTrace)
                }
            }
        }
        q.clear()

        correctTransCnt(dstn)
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
    private fun traceRoute(src: Int, dstn: Int){
        var cur = dstn

        while(cur != src) {
            stack.push(cur)
            cur = trace[cur]
        }
        stack.push(cur)
    }

    // 환승역 개수 보정
    private fun correctTransCnt(dstn: Int) {
        // 도착역은 환승역이어도 환승으로 계산 안 함
        val s = Runnable {
            if(db!!.roomStationDao().getStationTransSt(dstn) == 1){
                infoArrTime[3] -= 1
                infoArrDist[3] -= 1
                infoArrFare[3] -= 1
            }
        }
        val thread = Thread(s)
        thread.start()
    }
}

data class Node(val index: Int, val cost: Int) : Comparable<Node> {
    override fun compareTo(other: Node): Int = cost-other.cost
}