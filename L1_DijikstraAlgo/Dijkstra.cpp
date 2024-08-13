#include <bits/stdc++.h>
using namespace std;

const long long inf = 1e18;

int n; // no of nodes
vector<vector<pair<int,int>>> adj;

vector<long long > dijkstra(int src) {
    vector<long long> d(n+1,inf);

    d[src] = 0;

    // Set on unmarked nodes (and their distance)
    set<pair<long long ,  int > > s; 
    for ( int i  =  1 ; i <= n  ; i++)
    {
        s.insert({d[i] , i });
    }
    
    while(!s.empty()) {
       int cur = s.begin()->second ;  // shortest distance wali unmarked node 

       s.erase(s.begin()); // mark curr node 


       // relaxations  on neighbours

       for( pair<int , int > e : adj[cur])
      {
        int nb = e.first ;
        int w = e.second ; 

        if( d[cur] + w  < d[nb])
         {
            s.erase({d[nb] , nb}); // erase purana wala 
            d[nb] = d[cur] + w ;   // update it 
            s.insert( { d[n] , nb}); // insert new one 
         }

      }

        
       
    }

    for(long long &val : d)
        if(val == inf)
            val = -1;
    
    return d;
}

int main() {
    int m ;  // no of edges 
     cin >> n >> m;
    adj.resize(n+1);
   
    while(m--) {
        int i, j, w; cin >> i >> j >> w;   // w -> weight 
        adj[i].push_back({j, w});  // for undirected 
        adj[j].push_back({i, w});
    }

    vector<long long > par = dijkstra(1);

   
    for( int i = 1  ; i<= n ; i++)
    {
        cout<< par[i];
    }

	return 0;
}