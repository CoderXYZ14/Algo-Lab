#include <iostream>
#include <string>
#include <algorithm>
using namespace std;

class LongestCommonSubsequence {
public:
    int longestCommonSubsequence(string text1,string text2) {
        int n=text1.length();
        int m=text2.length();
        int dp[n+1][m+1];
        for(int i=0;i<=n;i++)
            dp[i][0]=0;
        for(int j=0;j<=m;j++)
            dp[0][j]=0;
        // Fill the dp array and print intermediate steps
        for(int i=1;i<=n;i++) {
            for(int j=1;j<=m;j++){
                if(text1[i-1]==text2[j-1])
                    dp[i][j]=1+dp[i-1][j-1];
                else
                    dp[i][j]=max(dp[i-1][j],dp[i][j-1]);

                // Print the current state of dp table
                cout << "dp[" << i << "][" << j << "] = " << dp[i][j] << " (comparing '" << text1[i - 1] << "' and '" << text2[j - 1] << "')" << endl;
            }
        }
        // Print the final dp table
        cout << "\nFinal DP table:" << endl;
        for(int i=0;i<=n;i++){
            for (int j=0;j<= m;j++)
                cout << dp[i][j] << " ";
            cout << endl;
        }
        return dp[n][m];
    }
};

int main() {
    LongestCommonSubsequence lcs;
    string text1, text2;
    // User input for the two strings
    cout << "Enter the first string: ";
    cin >> text1;
    cout << "Enter the second string: ";
    cin >> text2;

    // Calculate and display the length of the longest common subsequence
    int result = lcs.longestCommonSubsequence(text1, text2);
    cout << "Length of Longest Common Subsequence: " << result << endl;
    return 0;
}
