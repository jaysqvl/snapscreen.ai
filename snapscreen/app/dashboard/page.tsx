"use client"

import { AppSidebar } from "@/components/app-sidebar"
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from "@/components/ui/sidebar"
import { Separator } from "@/components/ui/separator"
import { FileBarChart, Plus } from "lucide-react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { useState, useRef } from "react"

export default function DashboardPage() {
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const fileInputRef = useRef<HTMLInputElement>(null)
  
  // Handle file selection
  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      // In a real app, we would process the file here
      console.log("File selected:", file.name);
      // You could read the file content and set it in state
    }
  };

  // Trigger file input click
  const triggerFileUpload = () => {
    fileInputRef.current?.click();
  };
  
  return (
    <SidebarProvider
      style={{
        "--sidebar-width": "18rem",
      } as React.CSSProperties}
    >
      <AppSidebar />
      <SidebarInset className="flex flex-1 flex-col">
        <main className="flex flex-1 flex-col overflow-auto">
          <div className="flex-1 flex items-center justify-center">
            <div className="mx-auto max-w-6xl flex flex-col items-center justify-center space-y-6">
              <div className="flex flex-col gap-2 text-center">
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
                    <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                      <DialogTrigger asChild>
                        <Button>
                          <Plus className="mr-2 h-4 w-4" />
                          New Scan
                        </Button>
                      </DialogTrigger>
                      <DialogContent className="sm:max-w-6xl p-4 w-[95vw]">
                        <DialogHeader className="pb-2">
                          <DialogTitle>New scan</DialogTitle>
                        </DialogHeader>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-2">
                          <div className="space-y-2">
                            <h3 className="text-base font-medium">Resume</h3>
                            <div className="border rounded-lg p-3 h-[550px] flex flex-col">
                              <textarea 
                                placeholder="Paste resume text..." 
                                className="flex-1 resize-none border-0 bg-transparent p-0 outline-none"
                              />
                              <div className="border-t pt-3 mt-2">
                                <input
                                  type="file"
                                  ref={fileInputRef}
                                  className="hidden"
                                  accept=".pdf,.docx"
                                  onChange={handleFileUpload}
                                />
                                <Button 
                                  variant="outline" 
                                  className="w-full"
                                  onClick={triggerFileUpload}
                                >
                                  <Plus className="mr-2 h-4 w-4" />
                                  Drag & Drop or Upload
                                </Button>
                                <div className="text-xs text-muted-foreground mt-1 text-center">
                                  Supported formats: PDF, DOCX
                                </div>
                              </div>
                            </div>
                          </div>
                          <div className="space-y-2">
                            <h3 className="text-base font-medium">Job Description</h3>
                            <div className="border rounded-lg p-3 h-[550px]">
                              <textarea 
                                placeholder="Copy and paste job description here" 
                                className="h-full w-full resize-none border-0 bg-transparent p-0 outline-none"
                              />
                            </div>
                          </div>
                        </div>
                        <div className="flex justify-end items-center pt-4">
                          <Button size="lg">Scan</Button>
                        </div>
                      </DialogContent>
                    </Dialog>
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