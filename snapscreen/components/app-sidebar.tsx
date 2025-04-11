"use client"

import { Button } from "@/components/ui/button"
import { ScrollArea } from "@/components/ui/scroll-area"
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarTrigger,
} from "@/components/ui/sidebar"
import { FileText, Home, Plus, User } from "lucide-react"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { useCallback, useMemo, useState } from "react"
import { Input } from "@/components/ui/input"
import { cn } from "@/lib/utils"
import Link from "next/link"

// Demo scans - replace with actual data in production
const RESUME_SCANS = [
  { id: "1", title: "Software Engineer Resume", date: "2023-10-15", score: 82 },
  { id: "2", title: "Product Manager Application", date: "2023-09-22", score: 75 },
  { id: "3", title: "Data Scientist Position", date: "2023-08-30", score: 68 },
  { id: "4", title: "Frontend Developer", date: "2023-07-14", score: 91 },
  { id: "5", title: "UX Designer", date: "2023-06-08", score: 88 },
]

export function AppSidebar() {
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [selectedScanId, setSelectedScanId] = useState<string | null>(null)
  const [searchQuery, setSearchQuery] = useState("")

  // Filter scans based on search
  const filteredScans = useMemo(() => {
    if (!searchQuery.trim()) return RESUME_SCANS
    
    const lowercaseQuery = searchQuery.toLowerCase()
    return RESUME_SCANS.filter(scan => 
      scan.title.toLowerCase().includes(lowercaseQuery)
    )
  }, [searchQuery])

  // Handle scan selection
  const handleSelectScan = useCallback((scanId: string) => {
    setSelectedScanId(scanId)
    // Here you would also trigger any necessary data fetching
    // or state updates in a real application
  }, [])

  return (
    <Sidebar>
      <SidebarContent>
        <SidebarGroup>
          <div className="flex items-center justify-between px-4 py-2">
            <SidebarGroupLabel>Resume Scans</SidebarGroupLabel>
            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
              <DialogTrigger asChild>
                <Button size="sm" variant="outline">
                  <Plus className="mr-1 h-3.5 w-3.5" />
                  New Scan
                </Button>
              </DialogTrigger>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>Create New Resume Scan</DialogTitle>
                  <DialogDescription>
                    Upload your resume and job description to get insights.
                  </DialogDescription>
                </DialogHeader>
                {/* Form would go here - to be implemented later */}
                <div className="space-y-4 py-2">
                  <p className="text-sm text-muted-foreground">
                    Resume upload and job description form will be added here.
                  </p>
                </div>
              </DialogContent>
            </Dialog>
          </div>
          <div className="px-3 py-2">
            <Input 
              placeholder="Search scans..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="h-8"
            />
          </div>
          <SidebarGroupContent>
            <ScrollArea className="h-[calc(100vh-14rem)]">
              {filteredScans.length > 0 ? (
                <SidebarMenu>
                  {filteredScans.map((scan) => (
                    <SidebarMenuItem key={scan.id}>
                      <SidebarMenuButton 
                        asChild
                        className={cn(
                          selectedScanId === scan.id && "bg-accent"
                        )}
                        onClick={() => handleSelectScan(scan.id)}
                      >
                        <div className="flex w-full cursor-pointer items-center">
                          <FileText className="h-4 w-4 shrink-0" />
                          <div className="ml-2 flex flex-1 flex-col items-start overflow-hidden">
                            <span className="truncate font-medium">{scan.title}</span>
                            <div className="flex w-full items-center justify-between">
                              <span className="text-xs text-muted-foreground">{scan.date}</span>
                              <span className={cn(
                                "text-xs font-medium",
                                scan.score >= 80 ? "text-green-500" : 
                                scan.score >= 70 ? "text-yellow-500" : "text-red-500"
                              )}>
                                {scan.score}%
                              </span>
                            </div>
                          </div>
                        </div>
                      </SidebarMenuButton>
                    </SidebarMenuItem>
                  ))}
                </SidebarMenu>
              ) : (
                <div className="flex h-32 flex-col items-center justify-center px-4 text-center">
                  <p className="text-sm text-muted-foreground">No matching scans found</p>
                </div>
              )}
            </ScrollArea>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  )
} 