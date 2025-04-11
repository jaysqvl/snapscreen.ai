"use client"

import { AppSidebar } from "@/components/app-sidebar"
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from "@/components/ui/sidebar"
import { Separator } from "@/components/ui/separator"
import { FileBarChart } from "lucide-react"
import Link from "next/link"
import { Button } from "@/components/ui/button"

export default function DashboardPage() {
  return (
    <SidebarProvider
      style={{
        "--sidebar-width": "18rem",
      } as React.CSSProperties}
    >
      <AppSidebar />
      <SidebarInset className="flex flex-1 flex-col">
        <main className="flex flex-1 flex-col overflow-auto">
          <div className="flex h-14 items-center border-b px-4 lg:h-[57px]">
            <SidebarTrigger className="lg:hidden" />
            <div className="ml-4">
              <h1 className="text-lg font-medium">Dashboard</h1>
            </div>
          </div>
          <div className="flex-1 overflow-auto p-4 md:p-6">
            <div className="mx-auto max-w-6xl space-y-6">
              <div className="flex flex-col gap-2">
                <h2 className="text-2xl font-bold tracking-tight">Welcome to SnapScreen</h2>
                <p className="text-muted-foreground">
                  Select a resume scan from the sidebar or create a new one to get started.
                </p>
              </div>
              <Separator />
              
              {/* Empty state when no scan is selected */}
              <div className="flex min-h-[400px] flex-col items-center justify-center rounded-lg border border-dashed text-center">
                <div className="mx-auto flex max-w-md flex-col items-center justify-center space-y-4 p-6">
                  <div className="rounded-full bg-primary/10 p-3">
                    <FileBarChart className="h-6 w-6 text-primary" />
                  </div>
                  <h3 className="text-xl font-semibold">No Resume Scan Selected</h3>
                  <p className="text-sm text-muted-foreground">
                    Select a resume scan from the sidebar to view detailed analysis, or create a new scan to get insights on your resume.
                  </p>
                  <div className="flex gap-2">
                    <Button variant="outline" asChild>
                      <Link href="/dashboard/progress">
                        View Progress
                      </Link>
                    </Button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </main>
      </SidebarInset>
    </SidebarProvider>
  )
} 