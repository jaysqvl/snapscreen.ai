"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { FileText, Building, BarChart } from "lucide-react"
import Link from "next/link"
import { useState, useEffect } from "react"

interface ActivityDay {
  date: Date;
  count: number;
}

interface CompanyApplication {
  company: string;
  count: number;
  averageScore: number;
}

// Mock data - replace with actual database data in production
const STATS = {
  totalScans: 5,
  averageScore: 74,
  recentScan: {
    title: "Software Engineer Resume",
    date: "October 15, 2023"
  },
  companyApplications: [
    { company: "Google", count: 3, averageScore: 87 },
    { company: "Microsoft", count: 2, averageScore: 82 },
    { company: "Amazon", count: 2, averageScore: 76 },
    { company: "Meta", count: 1, averageScore: 68 }
  ] as CompanyApplication[],
}

// Generate mock data for the heatmap
function generateMockActivityData(): ActivityDay[] {
  const today = new Date()
  const oneYearAgo = new Date(today)
  oneYearAgo.setFullYear(today.getFullYear() - 1)
  
  const days: ActivityDay[] = []
  for (let d = new Date(oneYearAgo); d <= today; d.setDate(d.getDate() + 1)) {
    // Random activity level (0-4) with most days being 0
    const random = Math.random()
    let activityLevel = 0
    
    if (random > 0.85) activityLevel = 1
    if (random > 0.92) activityLevel = 2
    if (random > 0.96) activityLevel = 3
    if (random > 0.98) activityLevel = 4
    
    days.push({
      date: new Date(d),
      count: activityLevel,
    })
  }
  return days
}

export default function ProgressPage() {
  const [activityData, setActivityData] = useState<ActivityDay[]>([]);
  
  // Generate activity data on client-side only
  useEffect(() => {
    setActivityData(generateMockActivityData());
  }, []);
  
  return (
    <div className="flex-1 p-6 md:p-8 lg:p-10">
      <div className="mx-auto max-w-5xl space-y-8">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold tracking-tight">Resume Progress</h1>
          <Button asChild>
            <Link href="/dashboard">
              <FileText className="mr-2 h-4 w-4" />
              View Scans
            </Link>
          </Button>
        </div>
        
        <div className="grid gap-6 md:grid-cols-3">
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">Total Resume Scans</CardTitle>
              <CardDescription>All-time scan count</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold">{STATS.totalScans}</div>
            </CardContent>
          </Card>
          
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">Average Score</CardTitle>
              <CardDescription>Across all scans</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold">{STATS.averageScore}%</div>
            </CardContent>
          </Card>
          
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">Recent Scan</CardTitle>
              <CardDescription>Last submitted resume</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="font-medium">{STATS.recentScan.title}</div>
              <div className="text-sm text-muted-foreground">{STATS.recentScan.date}</div>
            </CardContent>
          </Card>
        </div>
        
        <div className="space-y-4">
          <h2 className="text-xl font-semibold tracking-tight">Scan Activity</h2>
          <Card>
            <CardHeader>
              <CardTitle>Annual Contribution History</CardTitle>
              <CardDescription>Track your resume scanning activity over time</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="py-4">
                <ActivityHeatmap data={activityData} />
              </div>
            </CardContent>
          </Card>
        </div>
        
        <div className="space-y-4">
          <h2 className="text-xl font-semibold tracking-tight">Company Applications</h2>
          <Card>
            <CardHeader>
              <CardTitle>Applications by Company</CardTitle>
              <CardDescription>Track your applications across different companies</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {STATS.companyApplications.map((app, i) => (
                  <div key={i} className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <Building className="h-4 w-4 text-muted-foreground" />
                      <span className="font-medium">{app.company}</span>
                    </div>
                    <div className="flex items-center gap-4">
                      <div className="flex items-center gap-1">
                        <FileText className="h-4 w-4 text-muted-foreground" />
                        <span>{app.count}</span>
                      </div>
                      <div className="flex items-center gap-1">
                        <BarChart className="h-4 w-4 text-muted-foreground" />
                        <span className={app.averageScore >= 80 ? "text-green-500" : 
                                        app.averageScore >= 70 ? "text-yellow-500" : "text-red-500"}>
                          {app.averageScore}%
                        </span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}

interface ActivityHeatmapProps {
  data: ActivityDay[];
}

// GitHub-style contribution heatmap component
function ActivityHeatmap({ data }: ActivityHeatmapProps) {
  // Group data by month for display
  const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
  const days = ["", "Mon", "", "Wed", "", "Fri", ""]
  
  return (
    <div className="space-y-2">
      <div className="flex text-sm text-muted-foreground justify-between px-2">
        {months.map((month, i) => (
          <div key={i} className="flex-1 text-center">{month}</div>
        ))}
      </div>
      
      <div className="flex">
        <div className="flex flex-col gap-1 mr-2 text-xs text-muted-foreground">
          {days.map((day, i) => (
            <div key={i} className="h-4 flex items-center">{day}</div>
          ))}
        </div>
        
        <div className="grid grid-cols-52 gap-1 flex-1">
          {data.map((day: ActivityDay, i: number) => (
            <div 
              key={i} 
              className={`h-4 w-4 rounded-sm ${
                day.count === 0 ? "bg-muted" :
                day.count === 1 ? "bg-emerald-200 dark:bg-emerald-900" :
                day.count === 2 ? "bg-emerald-300 dark:bg-emerald-800" :
                day.count === 3 ? "bg-emerald-400 dark:bg-emerald-700" :
                "bg-emerald-500 dark:bg-emerald-600"
              }`}
              title={`${day.date.toDateString()}: ${day.count} scans`}
            />
          ))}
        </div>
      </div>
      
      <div className="flex items-center justify-end gap-2 text-xs">
        <span className="text-muted-foreground">Less</span>
        <div className="h-3 w-3 rounded-sm bg-muted" />
        <div className="h-3 w-3 rounded-sm bg-emerald-200 dark:bg-emerald-900" />
        <div className="h-3 w-3 rounded-sm bg-emerald-300 dark:bg-emerald-800" />
        <div className="h-3 w-3 rounded-sm bg-emerald-400 dark:bg-emerald-700" />
        <div className="h-3 w-3 rounded-sm bg-emerald-500 dark:bg-emerald-600" />
        <span className="text-muted-foreground">More</span>
      </div>
    </div>
  )
} 