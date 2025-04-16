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
import { FileText, Home, Plus, User, Edit, Trash, MoreVertical } from "lucide-react"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger, DialogFooter } from "@/components/ui/dialog"
import { useCallback, useMemo, useState, useRef, Dispatch, SetStateAction } from "react"
import { Input } from "@/components/ui/input"
import { cn } from "@/lib/utils"
import Link from "next/link"
import { 
  DropdownMenu, 
  DropdownMenuContent, 
  DropdownMenuItem, 
  DropdownMenuTrigger 
} from "@/components/ui/dropdown-menu"
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/components/ui/alert-dialog"

// Demo scans - replace with actual data in production
const RESUME_SCANS = [
  { id: "1", title: "Software Engineer Resume", date: "2023-10-15", score: 82, company: "Google" },
  { id: "2", title: "Product Manager Application", date: "2023-09-22", score: 75, company: "Microsoft" },
  { id: "3", title: "Data Scientist Position", date: "2023-08-30", score: 68, company: "Amazon" },
  { id: "4", title: "Frontend Developer", date: "2023-07-14", score: 91, company: "Meta" },
  { id: "5", title: "UX Designer", date: "2023-06-08", score: 88, company: "Apple" },
]

interface AppSidebarProps {
  onScanSelect?: Dispatch<SetStateAction<string | null>>;
  selectedScanId?: string | null;
}

export function AppSidebar({ onScanSelect, selectedScanId }: AppSidebarProps) {
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [searchQuery, setSearchQuery] = useState("")
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [deleteAlertOpen, setDeleteAlertOpen] = useState(false)
  const [editDialogOpen, setEditDialogOpen] = useState(false)
  const [scanToEdit, setScanToEdit] = useState<typeof RESUME_SCANS[0] | null>(null)
  const [scanToDelete, setScanToDelete] = useState<string | null>(null)

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
    if (onScanSelect) {
      // If the scan is already selected, deselect it (set to null)
      if (selectedScanId === scanId) {
        onScanSelect(null);
      } else {
        onScanSelect(scanId);
      }
    }
  }, [onScanSelect, selectedScanId])

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

  // Handle edit scan
  const handleEditScan = (scan: typeof RESUME_SCANS[0], e: React.MouseEvent) => {
    e.stopPropagation(); // Prevent triggering the scan selection
    setScanToEdit(scan);
    setEditDialogOpen(true);
  };

  // Handle delete scan
  const handleDeleteClick = (scanId: string, e: React.MouseEvent) => {
    e.stopPropagation(); // Prevent triggering the scan selection
    setScanToDelete(scanId);
    setDeleteAlertOpen(true);
  };

  // Confirm delete scan
  const confirmDelete = () => {
    if (scanToDelete) {
      // In a real app, we would call an API to delete the scan
      console.log("Deleting scan:", scanToDelete);
      // For now, just log it
      
      // Close the dialog
      setDeleteAlertOpen(false);
      setScanToDelete(null);
    }
  };

  return (
    <Sidebar>
      <SidebarContent>
        <SidebarGroup>
          <div className="flex items-center justify-between px-4 py-2">
            <SidebarGroupLabel>Resume Scans</SidebarGroupLabel>
          </div>
          
          <div className="px-3 pb-2 pt-3">
            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
              <DialogTrigger asChild>
                <Button size="default" variant="default" className="w-full mb-4">
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
                    <SidebarMenuItem key={scan.id} className="px-2 py-0.5">
                      <SidebarMenuButton 
                        asChild
                        className={cn(
                          "px-2 py-1 rounded-md h-auto",
                          selectedScanId === scan.id && "bg-accent"
                        )}
                        onClick={() => handleSelectScan(scan.id)}
                      >
                        <div className="flex w-full cursor-pointer items-center relative">
                          <FileText className="h-4 w-4 shrink-0" />
                          <div className="ml-2 flex flex-1 flex-col items-start overflow-hidden">
                            <span className="truncate font-medium w-full">{scan.title}</span>
                            <div className="text-sm font-medium text-primary/70 truncate w-full">{scan.company}</div>
                            <div className="flex w-full items-center justify-between pt-0">
                              <span className="text-xs text-muted-foreground">{scan.date}</span>
                              <span className={cn(
                                "text-xs font-medium w-10 text-right",
                                scan.score >= 80 ? "text-green-500" : 
                                scan.score >= 70 ? "text-yellow-500" : "text-red-500"
                              )}>
                                {scan.score}%
                              </span>
                            </div>
                          </div>
                          {selectedScanId === scan.id && (
                            <div className="absolute right-0 top-1/2 transform -translate-y-1/2 z-10">
                              <DropdownMenu>
                                <DropdownMenuTrigger asChild>
                                  <Button size="sm" variant="ghost" className="h-7 w-7 p-0">
                                    <MoreVertical className="h-4 w-4" />
                                  </Button>
                                </DropdownMenuTrigger>
                                <DropdownMenuContent align="end">
                                  <DropdownMenuItem onClick={(e) => handleEditScan(scan, e)}>
                                    <Edit className="mr-2 h-4 w-4" /> Edit
                                  </DropdownMenuItem>
                                  <DropdownMenuItem 
                                    className="text-destructive focus:text-destructive"
                                    onClick={(e) => handleDeleteClick(scan.id, e)}
                                  >
                                    <Trash className="mr-2 h-4 w-4" /> Delete
                                  </DropdownMenuItem>
                                </DropdownMenuContent>
                              </DropdownMenu>
                            </div>
                          )}
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

      {/* Edit Scan Dialog */}
      <Dialog open={editDialogOpen} onOpenChange={setEditDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Edit Scan</DialogTitle>
            <DialogDescription>
              Update the scan details below.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid grid-cols-4 items-center gap-4">
              <label htmlFor="title" className="text-right">Title</label>
              <Input
                id="title"
                value={scanToEdit?.title || ""}
                className="col-span-3"
                onChange={(e) => setScanToEdit(prev => prev ? {...prev, title: e.target.value} : prev)}
              />
            </div>
            <div className="grid grid-cols-4 items-center gap-4">
              <label htmlFor="company" className="text-right">Company</label>
              <Input
                id="company"
                value={scanToEdit?.company || ""}
                className="col-span-3"
                onChange={(e) => setScanToEdit(prev => prev ? {...prev, company: e.target.value} : prev)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button onClick={() => setEditDialogOpen(false)}>Save Changes</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={deleteAlertOpen} onOpenChange={setDeleteAlertOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Are you sure?</AlertDialogTitle>
            <AlertDialogDescription>
              This action cannot be undone. This will permanently delete this scan
              and remove it from our servers.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={() => setScanToDelete(null)}>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={confirmDelete} className="bg-destructive text-destructive-foreground">
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </Sidebar>
  )
} 