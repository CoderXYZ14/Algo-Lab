#include <iostream>
#include <string>
using namespace std;

class LongestCommonSubsequence
{
public:
    int longestCommonSubsequence(string text1, string text2)
    {
        int n = text1.length();
        int m = text2.length();
        int dp[n + 1][m + 1];

        for (int i = 0; i <= n; i++)
            dp[i][0] = 0;
        for (int j = 0; j <= m; j++)
            dp[0][j] = 0;

        for (int i = 1; i <= n; i++)
        {
            for (int j = 1; j <= m; j++)
            {
                if (text1[i - 1] == text2[j - 1])
                    dp[i][j] = 1 + dp[i - 1][j - 1];
                else
                    dp[i][j] = max(dp[i - 1][j], dp[i][j - 1]);
                cout << "dp[" << i << "][" << j << "] = " << dp[i][j] << " (comparing '" << text1[i - 1] << "' and '" << text2[j - 1] << "')" << endl;
            }
        }

        cout << "\nFinal DP table:" << endl;
        for (int i = 0; i <= n; i++)
        {
            for (int j = 0; j <= m; j++)
                cout << dp[i][j] << " ";
            cout << endl;
        }

        string lcs = "";
        int i = n, j = m;
        while (i > 0 && j > 0)
        {
            if (text1[i - 1] == text2[j - 1])
            {
                lcs += text1[i - 1];
                i--;
                j--;
            }
            else if (dp[i - 1][j] > dp[i][j - 1])
                i--;
            else
                j--;
        }

        reverse(lcs);
        cout << "Longest Common Subsequence: " << lcs << endl;

        return dp[n][m];
    }

    void reverse(string &str)
    {
        int left = 0;
        int right = str.length() - 1;
        while (left < right)
        {
            swap(str[left], str[right]);
            left++;
            right--;
        }
    }
};

int main()
{
    LongestCommonSubsequence lcs;
    string text1, text2;
    cout << "Enter the first string: ";
    cin >> text1;
    cout << "Enter the second string: ";
    cin >> text2;

    int result = lcs.longestCommonSubsequence(text1, text2);
    cout << "Length of Longest Common Subsequence: " << result << endl;
    return 0;
}
