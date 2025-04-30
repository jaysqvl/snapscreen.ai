"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { FileText, Building, BarChart } from "lucide-react"
import Link from "next/link"
import { useState, useEffect } from "react"
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip"

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
    title: "Software Engineer",
    company: "Google",
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
  // Use a fixed "today" date for development purposes
  const today = new Date(2025, 3, 17); // April 17th, 2025
  const oneYearAgo = new Date(today);
  oneYearAgo.setFullYear(today.getFullYear() - 1);
  
  const days: ActivityDay[] = [];
  for (let d = new Date(oneYearAgo); d <= today; d.setDate(d.getDate() + 1)) {
    // Random activity level (0-4) with most days being 0
    const random = Math.random();
    let activityLevel = 0;
    
    if (random > 0.85) activityLevel = 1;
    if (random > 0.92) activityLevel = 2;
    if (random > 0.96) activityLevel = 3;
    if (random > 0.98) activityLevel = 4;
    
    days.push({
      date: new Date(d),
      count: activityLevel,
    });
  }
  return days;
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
            <CardHeader className="pb-0">
              <CardTitle className="text-sm font-medium">Total Resume Scans</CardTitle>
              <CardDescription>All-time scan count</CardDescription>
            </CardHeader>
            <CardContent className="pb-0 pt-0 -mt-1">
              <div className="text-3xl font-bold">{STATS.totalScans}</div>
            </CardContent>
          </Card>
          
          <Card>
            <CardHeader className="pb-0">
              <CardTitle className="text-sm font-medium">Average Score</CardTitle>
              <CardDescription>Across all scans</CardDescription>
            </CardHeader>
            <CardContent className="pb-0 pt-0 -mt-1">
              <div className="text-3xl font-bold">{STATS.averageScore}%</div>
            </CardContent>
          </Card>
          
          <Card>
            <CardHeader className="pb-0">
              <CardTitle className="text-sm font-medium">Recent Scan</CardTitle>
              <CardDescription>Last submitted resume</CardDescription>
            </CardHeader>
            <CardContent className="pb-0 pt-0 -mt-1">
              <div className="text-xl font-bold -mt-1">{STATS.recentScan.title}</div>
              <div className="text-sm text-muted-foreground">
                {STATS.recentScan.company} • {STATS.recentScan.date}
              </div>
            </CardContent>
          </Card>
        </div>
        
        <div className="space-y-4">
          <h2 className="text-xl font-semibold tracking-tight">Scan Activity</h2>
          <Card>
            <CardHeader>
              <CardTitle>Annual Scan History</CardTitle>
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
  // Create a proper GitHub-style grid layout
  const days = ["", "Mon", "", "Wed", "", "Fri", ""];
  const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

  // Fixed "today" date for development consistency
  const today = new Date(2025, 3, 17); // April 17th, 2025
  
  // Create a complete calendar representation
  // Calculate the first day to show (1 year before today)
  const oneYearAgo = new Date(today);
  oneYearAgo.setFullYear(today.getFullYear() - 1);
  
  // Adjust to start on a Sunday (for proper week alignment)
  const startDate = new Date(oneYearAgo);
  const dayOfWeek = startDate.getDay(); // 0 = Sunday, 6 = Saturday
  startDate.setDate(startDate.getDate() - dayOfWeek); // Go back to previous Sunday
  
  // Create a week-based matrix (up to 53 weeks × 7 days)
  const weeks: (ActivityDay | null)[][] = [];
  
  // Starting from the adjusted date, fill all weeks
  const currentDate = new Date(startDate);
  while (currentDate <= today) {
    const weekData: (ActivityDay | null)[] = [];
    
    // Fill a week (7 days)
    for (let i = 0; i < 7; i++) {
      if (currentDate <= today) {
        // Find matching activity data for this date
        const activityForDay = data.find(day => 
          day.date.getFullYear() === currentDate.getFullYear() &&
          day.date.getMonth() === currentDate.getMonth() &&
          day.date.getDate() === currentDate.getDate()
        );
        
        if (activityForDay) {
          weekData.push(activityForDay);
        } else {
          // Create a zero-activity day
          weekData.push({
            date: new Date(currentDate),
            count: 0
          });
        }
      } else {
        // Future dates should be null (not rendered)
        weekData.push(null);
      }
      
      // Move to next day
      currentDate.setDate(currentDate.getDate() + 1);
    }
    
    weeks.push(weekData);
  }
  
  // Get month labels for the bottom axis
  const monthLabels: {month: string, position: number}[] = [];
  let currentMonth = -1;
  
  weeks.forEach((week, weekIndex) => {
    week.forEach(day => {
      if (day && day.date.getDate() <= 7) { // Only check first 7 days of each month
        const month = day.date.getMonth();
        if (month !== currentMonth) {
          monthLabels.push({
            month: months[month],
            position: weekIndex
          });
          currentMonth = month;
        }
      }
    });
  });
  
  return (
    <div className="space-y-4">
      <div className="flex">
        {/* Day labels on the left */}
        <div className="flex flex-col justify-between mr-2 text-xs text-muted-foreground h-[128px]">
          {days.map((day, i) => (
            <div key={i} className="h-4 pr-1 text-right">{day}</div>
          ))}
        </div>
        
        {/* Contribution grid */}
        <div className="flex-1">
          <div className="grid grid-flow-col gap-1 auto-cols-fr">
            {weeks.map((week, weekIndex) => (
              <div key={weekIndex} className="flex flex-col gap-1">
                {week.map((day, dayIndex) => 
                  day === null ? (
                    // Empty space for future dates
                    <div 
                      key={`${weekIndex}-${dayIndex}`} 
                      className="h-4 w-4"
                    />
                  ) : (
                    <Tooltip key={`${weekIndex}-${dayIndex}`}>
                      <TooltipTrigger asChild>
                        <div 
                          className={`h-4 w-4 rounded-sm ${
                            day.count === 0 ? "bg-muted" :
                            day.count === 1 ? "bg-emerald-200 dark:bg-emerald-900" :
                            day.count === 2 ? "bg-emerald-300 dark:bg-emerald-800" :
                            day.count === 3 ? "bg-emerald-400 dark:bg-emerald-700" :
                            "bg-emerald-500 dark:bg-emerald-600"
                          }`}
                        />
                      </TooltipTrigger>
                      <TooltipContent>
                        <p className="font-medium">{day.date.toLocaleDateString(undefined, { weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' })}</p>
                        <p>{day.count} {day.count === 1 ? 'scan' : 'scans'}</p>
                      </TooltipContent>
                    </Tooltip>
                  )
                )}
              </div>
            ))}
          </div>
          
          {/* Month labels at the bottom */}
          <div className="flex text-xs text-muted-foreground mt-1 relative">
            {monthLabels.map((label, i) => (
              <div 
                key={i} 
                className="absolute" 
                style={{ left: `${(label.position / weeks.length) * 100}%` }}
              >
                {label.month}
              </div>
            ))}
          </div>
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